package src.project;

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
}
