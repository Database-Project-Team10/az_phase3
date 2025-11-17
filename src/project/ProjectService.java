package src.project;

import src.member.Member;
import src.participant.ParticipantRepository;
import src.utils.Azconnection;
import src.techspec.ProjectTechspecRepository;
import src.techspec.MemberTechspecRepository;
import src.mbti.member.MemberMbtiRepository;
import src.mbti.project.ProjectMbtiRepository;
import src.mbti.MbtiDimension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectService {

    private final ProjectRepository projectRepository =  new ProjectRepository();
    private final ParticipantRepository participantRepository = new ParticipantRepository();
    private final MemberTechspecRepository techspecRepository = new MemberTechspecRepository();
    private final ProjectTechspecRepository projectTechspecRepository = new ProjectTechspecRepository();
    private final MemberMbtiRepository memberMbtiRepository = new MemberMbtiRepository();
    private final ProjectMbtiRepository projectMbtiRepository = new ProjectMbtiRepository();

    public List<Project> getProjectList(int cnt) {
        return projectRepository.findProjects(cnt);
    }

    public List<Project> getMyProjectList(Member currentMember) {
        return projectRepository.findProjectsByMemberId(currentMember.getId());
    }

    public Project getProjectDetail(Long projectId) {
        return projectRepository.findById(projectId);
    }

    // Controller가 호출하는 검증 메서드
    public Project getMyProjectById(Member currentUser, Long projectId) {
        return projectRepository.findMyProjectByIdAndMemberId(currentUser.getId(), projectId);
    }

    // 입력값을 파라미터로 받아서 처리
    public Project createProject(Member currentMember, String title, String description, Set<String> techNames, Map<Long, String> mbtiMap) {
        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. Project 생성
            Project newProject = new Project(title, description);
            long newProjectId = projectRepository.save(conn, newProject);

            // 2. Participant 추가
            participantRepository.saveLeader(conn, currentMember.getId(), newProjectId);

            // 3. 스택 저장
            if (!techNames.isEmpty()) {
                //System.out.println("\n[DB 저장 시작 - 스택]");
                for (String techName : techNames) {
                    Long techspecId = techspecRepository.findTechspecIdByName(techName);
                    if (techspecId == null) {
                        techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
                    }
                    projectTechspecRepository.addProjectTechspec(conn, newProjectId, techspecId);
                }
            }

            // 4. MBTI 저장
            if (!mbtiMap.isEmpty()) {
                //System.out.println("[DB 저장 시작 - MBTI]");
                projectMbtiRepository.saveProjectMbti(conn, newProjectId, mbtiMap);
            }

            conn.commit();
            System.out.println("\n'" + title + "' 프로젝트 생성 및 설정이 완료되었습니다.");
            return projectRepository.findProjectById(conn, newProjectId);

        } catch (SQLException e) {
            System.err.println("프로젝트 생성 중 오류 발생: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) {}
        }
        return null;
    }

    public boolean updateProjectInfo(Long projectId, String newTitle, String newDescription) {
        Project newProject = new Project(newTitle, newDescription);
        return projectRepository.updateProject(projectId, newProject);
    }

    public boolean deleteProject(Long projectId) {
        return projectRepository.deleteProject(projectId);
    }
}

