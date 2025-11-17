package src.techspec.project;

import src.techspec.exception.TechspecInvalidException;
import src.techspec.exception.TechspecAlreadyExistsException;
import src.techspec.exception.TechspecNotFoundException;
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

    public List<Techspec> getProjectTechspecs(Project currentProject) {
        return projectTechspecRepository.findTechspecsByProjectId(currentProject.getId());
    }

    public void addTechspecToProject(Project currentProject, String techName) {

        if (techName == null || techName.isBlank()) {
            throw new TechspecInvalidException("스택 이름은 비어 있을 수 없습니다.");
        }

        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false);

            Long techspecId = techspecRepository.findTechspecIdByName(techName);
            if (techspecId == null) {
                techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
            }

            boolean inserted = projectTechspecRepository.addProjectTechspec(
                    conn, currentProject.getId(), techspecId
            );

            if (!inserted) {
                throw new RuntimeException("스택 추가 실패");
            }

            conn.commit();

        } catch (SQLException e) {

            rollbackQuietly(conn);

            if (e.getErrorCode() == 1) {
                throw new TechspecAlreadyExistsException(
                        "이미 존재하는 스택입니다."
                );
            }

            throw new RuntimeException("DB 오류 발생: " + e.getMessage());

        } finally {
            closeQuietly(conn);
        }
    }

    public void removeTechspecFromProject(Project currentProject, Long techspecId) {

        if (!projectTechspecRepository.deleteProjectTechspec(currentProject.getId(), techspecId)) {
            throw new TechspecNotFoundException("삭제할 스택이 존재하지 않습니다.");
        }
    }

    private void rollbackQuietly(Connection conn) {
        try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
    }

    private void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ignored) {}
    }
}