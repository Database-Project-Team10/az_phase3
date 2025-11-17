package src.project;

import src.member.Member;
import src.participant.ParticipantRepository;
import src.utils.Azconnection;
import src.techspec.ProjectTechspecRepository;
import src.techspec.MemberTechspecRepository;
import src.techspec.Techspec;
import src.mbti.MemberMbtiRepository;
import src.mbti.ProjectMbtiRepository;
import src.mbti.MbtiDimension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.util.HashMap;
import java.util.Map;


public class ProjectService {

    private final ProjectRepository projectRepository =  new ProjectRepository();
    private final ParticipantRepository participantRepository = new ParticipantRepository();
    private final MemberTechspecRepository techspecRepository = new MemberTechspecRepository();
    private final ProjectTechspecRepository projectTechspecRepository = new ProjectTechspecRepository();
    private final MemberMbtiRepository memberMbtiRepository = new MemberMbtiRepository();
    private final ProjectMbtiRepository projectMbtiRepository = new ProjectMbtiRepository();

    Scanner scanner = new Scanner(System.in);

    public void showProjectList(int cnt) {
        List<Project> projectList = projectRepository.findProjects(cnt);
        System.out.println("---------- 프로젝트 목록 ----------");
        for (Project project : projectList) {
            System.out.println(project.getId() + ". " + project.getTitle());
        }
    }

    public void showMyProjectList(Member currentMember) {
        List<Project> projectList = projectRepository.findProjectsByMemberId(currentMember.getId());
        System.out.println("---------- 내 프로젝트 목록 ----------");
        for (Project project : projectList) {
            System.out.println(project.getId() + ". " + project.getTitle());
        }
    }

    public void showProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId);
        System.out.println("\n---------- 프로젝트 상세 정보 ----------");
        System.out.println("프로젝트명: " +  project.getTitle());
        System.out.println("\n프로젝트 설명");
        System.out.println(project.getDescription());
    }

    public boolean createProject(Member currentMember) {
        System.out.println("---------- 프로젝트 생성 ----------");
        System.out.print("프로젝트 제목: ");
        String title = scanner.nextLine();
        System.out.print("프로젝트 설명: ");
        String description = scanner.nextLine();

        // [!] 1. (수정) 입력을 List 대신 "Set"에 받습니다.
        // Set은 "C"와 "c"를 (대문자로 변환 시) 동일하게 보고, 중복을 허용하지 않습니다.
        Set<String> uniqueTechNames = new HashSet<>();
        System.out.println("\n---------- 요구 스택 추가 ----------");
        while (true) {
            System.out.print("추가할 기술 스택 이름 (완료: q): ");
            String techName = scanner.nextLine();

            if ("q".equalsIgnoreCase(techName)) {
                break; // q 입력 시 루프 종료
            }
            // DB에 저장하지 않고, "대문자"로 변환하여 Set에 추가
            uniqueTechNames.add(techName.toUpperCase());
        }

        Map<Long, String> newMbtiMap = new HashMap<>();
        List<MbtiDimension> dimensions = memberMbtiRepository.findAllMbtiDimensions();

        if (dimensions != null && !dimensions.isEmpty()) {
            System.out.println("\n---------- 선호 MBTI 설정 ----------");
            for (MbtiDimension dim : dimensions) {
                while (true) {
                    System.out.printf("%s (%s/%s): ", dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                    String input = scanner.nextLine().toUpperCase();
                    if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                        newMbtiMap.put(dim.getId(), input);
                        break;
                    }
                    System.out.println("잘못된 입력입니다.");
                }
            }
        }

        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // Project 생성
            Project newProject = new Project(title, description);
            long newProjectId = projectRepository.save(conn, newProject);

            // Participant 추가
            participantRepository.saveLeader(conn, currentMember.getId(), newProjectId);

            // 3. (수정) 중복이 제거된 "uniqueTechNames" Set을 사용
            if (!uniqueTechNames.isEmpty()) { // [!] 추가할 스택이 있을 때만 실행
                System.out.println("\n[DB 저장 시작]");
                for (String techName : uniqueTechNames) { // "C", "c"가 "C" 하나로 합쳐짐

                    // (작업 3) "Java" 이름으로 ID 찾기 (대소문자 무시)
                    Long techspecId = techspecRepository.findTechspecIdByName(techName);

                    // (작업 4) 없으면 새로 만들기 (대문자로 저장)
                    if (techspecId == null) {
                        System.out.println("'" + techName + "' 스택을 마스터 테이블에 새로 등록합니다.");
                        techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
                    }

                    // (작업 5) "프로젝트-스택" 연결
                    projectTechspecRepository.addProjectTechspec(conn, newProjectId, techspecId);

                    System.out.println("'" + techName + "' 스택이 처리되었습니다.");
                }
            }

            if (!newMbtiMap.isEmpty()) {
                projectMbtiRepository.saveProjectMbti(conn,newProjectId, newMbtiMap);
            }

            conn.commit();

            System.out.println("\n'" + title + "' 프로젝트 생성 및 설정이 완료되었습니다.");
            return true;

        } catch (SQLException e) {
            System.err.println("프로젝트 생성 중 오류 발생: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Auto-Commit 원상복구
                    conn.close(); // Connection 반환
                }
            } catch (SQLException e) {
                System.err.println("Connection 종료 실패: " + e.getMessage());
            }
        }
        return false;
    }
    public boolean updateProject(Long projectId) {
        Project project = projectRepository.findById(projectId);

        while (true){
            System.out.println("\n---------- [" + project.getTitle() + "] 수정 메뉴 ----------");
            System.out.println("1. 프로젝트 정보 수정 (제목/설명)");
            System.out.println("2. 요구 스택 추가");
            System.out.println("3. 요구 스택 삭제");
            System.out.println("4. 선호 MBTI 수정");
            System.out.println("b. 뒤로 가기 (프로젝트 메뉴)");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // (1) 프로젝트 정보(제목/설명) 수정
                    this.updateProjectDetails(project);
                    break;
                case "2":
                    // (2) 요구 스택 추가
                    this.addTechspecToProject(project.getId());
                    break;
                case "3":
                    // (3) 요구 스택 삭제
                    this.removeTechspecFromProject(project.getId());
                    break;
                case "4":
                    this.updateProjectMbti(project.getId());
                    break;
                case "b":
                    return true; // 수정 작업이 끝났으므로 true 반환
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }

    }

    private void updateProjectDetails(Project project) {
        System.out.println("---------- 프로젝트 정보 수정 ----------");
        String newTitle = project.getTitle();
        String newDescription = project.getDescription();

        while (true){
            System.out.print("제목을 수정하시겠습니까? (Y/N) ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                System.out.print("제목을 입력해주세요: ");
                newTitle = scanner.nextLine();
                break;
            } else if (choice.equalsIgnoreCase("N")) {
                break;
            } else { System.out.println("잘못된 입력입니다."); }
        }
        while (true){
            System.out.print("설명을 수정하시겠습니까? (Y/N) ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                System.out.print("설명을 입력해주세요: ");
                newDescription = scanner.nextLine();
                break;
            } else if (choice.equalsIgnoreCase("N")) {
                break;
            } else { System.out.println("잘못된 입력입니다."); }
        }

        Project newProject = new Project(newTitle, newDescription);
        boolean isSuccess = projectRepository.updateProject(project.getId(), newProject);

        if(isSuccess) {
            System.out.println("프로젝트 정보가 수정되었습니다.");
            // (선택) project 객체의 내용을 동기화
            project.setTitle(newTitle);
            project.setDescription(newDescription);
        } else {
            System.out.println("프로젝트 정보 수정에 실패했습니다.");
        }
    }

    private void addTechspecToProject(Long projectId) {
        Set<String> uniqueTechNames = new HashSet<>();
        System.out.println("\n---------- 요구 스택 추가 ----------");
        while (true) {
            System.out.print("추가할 기술 스택 이름 (완료: q): ");
            String techName = scanner.nextLine();

            if ("q".equalsIgnoreCase(techName)) {
                break; // q 입력 시 루프 종료
            }
            // [!] 대문자로 변환하여 Set에 추가
            uniqueTechNames.add(techName.toUpperCase());
        }

        if (uniqueTechNames.isEmpty()) { // [!] 추가할 게 없으면 바로 종료
            System.out.println("추가를 취소했습니다.");
            return;
        }

        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false);

            System.out.println("\n[DB 저장 시작]");
            // [!] 중복이 제거된 "uniqueTechNames" Set을 사용
            for (String techName : uniqueTechNames) {

                // (작업 1) "Java" 이름으로 ID 찾기 (대소문자 무시)
                Long techspecId = techspecRepository.findTechspecIdByName(techName);

                // (작업 2) 없으면 새로 만들기 (대문자로 저장)
                if (techspecId == null) {
                    System.out.println("'" + techName + "' 스택을 마스터 테이블에 새로 등록합니다.");
                    techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
                }

                // (작업 3) "프로젝트-스택" 연결
                // [!] ORA-00001 (중복) 오류가 날 수 있는 유일한 지점
                projectTechspecRepository.addProjectTechspec(conn, projectId, techspecId);

                System.out.println("'" + techName + "' 스택이 처리되었습니다.");
            }

            conn.commit(); // [!] 스택 추가 트랜잭션 커밋
            System.out.println("\n요구 스택 추가가 완료되었습니다.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001
                System.out.println("오류: 입력한 스택 중 일부가 이미 이 프로젝트에 추가되어 있습니다.");
            } else {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }
            try {
                if (conn != null) conn.rollback(); // [!] 스택 추가 트랜잭션 롤백
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Connection 종료 실패: " + e.getMessage());
            }
        }
    }

    private void removeTechspecFromProject(Long projectId) {
        System.out.println("\n---------- 요구 스택 삭제 ----------");

        // 1. 현재 목록을 보여줌
        List<Techspec> currentTechs = projectTechspecRepository.findTechspecsByProjectId(projectId);

        if (currentTechs.isEmpty()) {
            System.out.println("(삭제할 스택이 없습니다.)");
            return;
        }

        for (Techspec tech : currentTechs) {
            System.out.println(tech.getId() + ". " + tech.getName());
        }
        System.out.println("----------------------------------------");
        System.out.print("삭제할 기술 스택의 번호(ID)를 입력하세요 (취소: b): ");
        String idInput = scanner.nextLine();

        if ("b".equalsIgnoreCase(idInput)) {
            System.out.println("삭제를 취소했습니다.");
            return;
        }

        try {
            Long idToDelete = Long.parseLong(idInput);

            // 2. ID로 삭제
            boolean isSuccess = projectTechspecRepository.deleteProjectTechspec(projectId, idToDelete);

            if (isSuccess) {
                System.out.println("ID: " + idToDelete + " 스택이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("오류: ID " + idToDelete + "(은)는 이 프로젝트의 스택이 아닙니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 숫자를 입력해야 합니다.");
        }
    }

    public boolean deleteProject(Long projectId) {
        while (true){
            System.out.print("정말로 삭제하시겠습니까? (Y/N) ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                return projectRepository.deleteProject(projectId);
            }
            else if (choice.equalsIgnoreCase("N")) {
                break;
            }
            else{
                System.out.println("잘못된 입력입니다.");
            }
        }
        return false;
    }

    /**
     * (ProjectController가 호출)
     * Repository를 호출하여, 현재 회원이 해당 프로젝트에 참여했는지 검증합니다.
     *
     * @param currentUser 현재 로그인한 회원
     * @param projectId 사용자가 선택한 프로젝트 ID
     * @return 참여한 프로젝트가 맞으면 Project 객체, 아니면 null
     */
    public Project getMyProjectById(Member currentUser, Long projectId) {
        // [!] Repository에 이미 만들어둔 "findMyProjectByIdAndMemberId" 호출
        Project project = projectRepository.findMyProjectByIdAndMemberId(currentUser.getId(), projectId);
        return project; // 찾았으면 Project 객체, 못 찾았으면 null 반환
    }

    private void updateProjectMbti(Long projectId) {
        System.out.println("\n---------- 선호 MBTI 수정 ----------");

        List<MbtiDimension> dimensions = memberMbtiRepository.findAllMbtiDimensions();
        Map<Long, String> currentMbti = projectMbtiRepository.findMbtiMapByProjectId(projectId);

        System.out.print("현재 설정: ");
        for (MbtiDimension dim : dimensions) {
            System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
        }
        System.out.println();

        Map<Long, String> newMbtiMap = new HashMap<>();
        for (MbtiDimension dim : dimensions) {
            while (true) {
                System.out.printf("%s (%s/%s): ", dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                String input = scanner.nextLine().toUpperCase();
                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input);
                    break;
                }
                System.out.println("잘못된 입력입니다.");
            }
        }

        if (projectMbtiRepository.saveProjectMbti(projectId, newMbtiMap)) {
            System.out.println("MBTI 수정이 완료되었습니다.");
        } else {
            System.out.println("수정에 실패했습니다.");
        }
    }
}
