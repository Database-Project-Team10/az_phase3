package src.project;

import src.member.Member;
import src.participant.ParticipantRepository;
import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProjectService {

    private final ProjectRepository projectRepository =  new ProjectRepository();
    private final ParticipantRepository participantRepository = new ParticipantRepository();

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

    public void createProject(Member currentMember) {
        System.out.println("---------- 프로젝트 생성 ----------");
        System.out.print("프로젝트 제목: ");
        String title = scanner.nextLine();
        System.out.print("프로젝트 설명: ");
        String description = scanner.nextLine();

        Connection conn = null;
        try {
            // 1. Connection 가져오기
            conn = Azconnection.getConnection();
            if (conn == null) {
                throw new SQLException("DB 연결에 실패했습니다.");
            }

            // 2. [핵심] Auto-Commit 비활성화
            conn.setAutoCommit(false);

            // 3. 작업 1: Project 생성 (Repository에 conn 전달)
            Project newProject = new Project(title, description);
            long newProjectId = projectRepository.save(conn, newProject);

            // 4. 작업 2: Participant(Leader) 추가 (Repository에 conn 전달)
            participantRepository.saveLeader(conn, currentMember.getId(), newProjectId);
            // 5. [핵심] 모든 작업 성공 시 Commit
            conn.commit();

            System.out.println("'" + title + "' 프로젝트가 생성되었으며, "
                    + currentMember.getEmail() + "님이 리더로 지정되었습니다.");

        } catch (SQLException e) {
            // 6. [핵심] 작업 중 오류 발생 시 Rollback
            System.err.println("프로젝트 생성 중 오류 발생: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            // 7. [핵심] Connection 반환 (반드시 auto-commit 원상복구)
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Auto-Commit 원상복구
                    conn.close(); // Connection 반환
                }
            } catch (SQLException e) {
                System.err.println("Connection 종료 실패: " + e.getMessage());
            }
        }

    }

    public boolean updateProject(Long projectId) {
        Project project = projectRepository.findById(projectId);
        System.out.println("---------- 프로젝트 수정 ----------");

        String newTitle = project.getTitle();
        String newDescription = project.getDescription();

        while (true){
            System.out.print("제목을 수정하시겠습니까? (Y/N) ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                System.out.print("제목을 입력해주세요: ");
                newTitle = scanner.nextLine();
                break;
            }
            else if (choice.equalsIgnoreCase("N")) {
                break;
            }
            else{
                System.out.println("잘못된 입력입니다.");
            }
        }

        while (true){
            System.out.print("설명을 수정하시겠습니까? (Y/N) ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                System.out.print("설명을 입력해주세요: ");
                newDescription = scanner.nextLine();
                break;
            }
            else if (choice.equalsIgnoreCase("N")) {
                break;
            }
            else{
                System.out.println("잘못된 입력입니다.");
            }
        }

        Project newProject = new Project(newTitle, newDescription);
        return projectRepository.updateProject(projectId, newProject);

    }
}
