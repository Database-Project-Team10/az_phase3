package src.project;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProjectService {
    private final ProjectRepository projectRepository =  new ProjectRepository();
    Scanner scanner = new Scanner(System.in);

    public void showProjectList(int cnt) {
        List<String> projectList = projectRepository.findProjects(cnt);
        System.out.println("---------- 프로젝트 목록 ----------");
        for (int i = 0; i < projectList.size(); i++) {
            System.out.println(i+1 + ". " + projectList.get(i));
        }
        System.out.println("q 키를 누르면 나가집니다.");
    }

    public void createProject() {
        System.out.println("---------- 프로젝트 생성 ----------");
        System.out.print("프로젝트 제목: ");
        String title = scanner.nextLine();
        System.out.print("프로젝트 설명: ");
        String description = scanner.nextLine();

        Project newProject = new Project(title, description);

        try {
            projectRepository.save(newProject);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("저장 실패: 아이디 중복 (DB)");
            } else {
                System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
            }
        }

    }
}
