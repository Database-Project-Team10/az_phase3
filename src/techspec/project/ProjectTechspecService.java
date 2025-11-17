package src.techspec.project;

import src.project.Project;
import src.techspec.Techspec;
import src.techspec.TechspecRepository;
import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjectTechspecService {
    private final ProjectTechspecRepository projectTechspecRepository = new ProjectTechspecRepository();
    private final TechspecRepository techspecRepository = new TechspecRepository();

    public void viewProjectTechspecs(Project currentProject) {
        System.out.println("\n---------- [" + currentProject.getTitle() + "] 요구 스택 목록 ----------");

        List<Techspec> projectTechs = projectTechspecRepository.findTechspecsByProjectId(currentProject.getId());

        if (projectTechs.isEmpty()) {
            System.out.println("(아직 등록된 요구 스택이 없습니다.)");
        } else {
            for (Techspec tech : projectTechs) {
                System.out.println(tech.getId() + ". " + tech.getName());
            }
        }
    }

    public void addTechspecToProject(Project currentProject, String techName) {

        Long techspecId = techspecRepository.findTechspecIdByName(techName);

        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false);

            if (techspecId == null) {
                System.out.println("'" + techName + "' 스택을 마스터 테이블에 새로 등록합니다.");
                techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
            }

            boolean isSuccess = projectTechspecRepository.addProjectTechspec(conn, currentProject.getId(), techspecId);


            conn.commit();

            if (isSuccess) {
                System.out.println("'" + techName + "' 스택이 [프로젝트]에 성공적으로 추가되었습니다.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001 (Unique Constraint)
                System.out.println("오류: '" + techName + "'(은)는 이미 이 프로젝트에 추가된 스택입니다.");
            } else {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }
            try {
                if (conn != null) conn.rollback();
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

    public void removeTechspecFromProject(Project currentProject, Long techspecId) {

        boolean isSuccess = projectTechspecRepository.deleteProjectTechspec(currentProject.getId(), techspecId);

        if (isSuccess) {
            System.out.println("ID: " + techspecId + " 스택이 [프로젝트]에서 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("오류: ID " + techspecId + "(은)는 이 프로젝트에 없거나, 삭제에 실패했습니다.");
        }
    }
}
