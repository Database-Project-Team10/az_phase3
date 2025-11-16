package src.techspec;

import src.project.Project;
import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjectTechspecService {
    // [!] 2개의 Repository를 모두 생성
    private final ProjectTechspecRepository projectTechspecRepository = new ProjectTechspecRepository();
    // (재사용) Techspec 마스터 테이블을 다루기 위해 필요
    private final MemberTechspecRepository techspecRepository = new MemberTechspecRepository();

    /**
     * 1. 프로젝트 스택 목록 보기 (R)
     * (Repository가 conn을 관리)
     */
    public void viewProjectTechspecs(Project currentProject) {
        System.out.println("\n---------- [" + currentProject.getTitle() + "] 요구 스택 목록 ----------");

        // Repository의 "프로젝트ID로 찾기" 호출
        List<Techspec> projectTechs = projectTechspecRepository.findTechspecsByProjectId(currentProject.getId());

        if (projectTechs.isEmpty()) {
            System.out.println("(아직 등록된 요구 스택이 없습니다.)");
        } else {
            for (Techspec tech : projectTechs) {
                System.out.println(tech.getId() + ". " + tech.getName());
            }
        }
    }

    /**
     * 2. 스택 추가 (C)
     * (Service가 conn을 관리 - 트랜잭션)
     */
    public void addTechspecToProject(Project currentProject, String techName) {

        // 1. (재사용) TechspecRepository를 사용해 마스터 테이블에 기술이 있는지 확인
        Long techspecId = techspecRepository.findTechspecIdByName(techName);

        Connection conn = null;
        try {
            // 2. [트랜잭션 시작]
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false);

            // 3. [비즈니스 로직] 기술 ID가 없었다면(null),
            //    (재사용) TechspecRepository를 이용해 "새 기술"을 마스터 테이블에 INSERT
            if (techspecId == null) {
                System.out.println("'" + techName + "' 스택을 마스터 테이블에 새로 등록합니다.");
                techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
            }

            // 4. [핵심 작업] "프로젝트-기술" 연결을 INSERT (ProjectTechspecRepository 사용)
            boolean isSuccess = projectTechspecRepository.addProjectTechspec(conn, currentProject.getId(), techspecId);

            // 5. [트랜잭션 성공] commit
            conn.commit();

            if (isSuccess) {
                System.out.println("'" + techName + "' 스택이 [프로젝트]에 성공적으로 추가되었습니다.");
            }

        } catch (SQLException e) {
            // [트랜잭션 롤백]
            if (e.getErrorCode() == 1) { // ORA-00001 (Unique Constraint)
                System.out.println("오류: '" + techName + "'(은)는 이미 이 프로젝트에 추가된 스택입니다.");
            } else {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }
            try {
                if (conn != null) conn.rollback(); // [!] 모든 작업을 취소
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            // [Connection 반환]
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

    /**
     * 3. 스택 삭제 (D)
     * (Repository가 conn을 관리)
     */
    public void removeTechspecFromProject(Project currentProject, Long techspecId) {

        // [!] "ID로 삭제"는 트랜잭션이 필요 없으므로 Repository 호출만 하면 끝
        boolean isSuccess = projectTechspecRepository.deleteProjectTechspec(currentProject.getId(), techspecId);

        if (isSuccess) {
            System.out.println("ID: " + techspecId + " 스택이 [프로젝트]에서 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("오류: ID " + techspecId + "(은)는 이 프로젝트에 없거나, 삭제에 실패했습니다.");
        }
    }
}
