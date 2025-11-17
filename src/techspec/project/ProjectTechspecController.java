package src.techspec.project;

import src.project.Project;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

public class ProjectTechspecController {

    private final ProjectTechspecService projectTechspecService = new ProjectTechspecService();
    private final Scanner scanner = new Scanner(System.in);

    public void showProjectTechspecs(Project currentProject) {
        projectTechspecService.viewProjectTechspecs(currentProject);
    }

    public void showProjectTechspecMenu(Project currentProject) {
        while(true) {
            System.out.println("\n---------- [" + currentProject.getTitle() + "] 요구 테크스펙 관리 ----------");
            System.out.println("1. 요구 테크스펙 목록 보기");
            System.out.println("2. 테크스펙 추가");
            System.out.println("3. 테크스펙 삭제 ");
            System.out.println("b. 뒤로 가기 (상세 메뉴)");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    projectTechspecService.viewProjectTechspecs(currentProject);
                    break;
                case "2":
                    this.addTechspecUI(currentProject);
                    break;
                case "3":
                    this.removeTechspecUI(currentProject);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public void addTechspecUI(Project currentProject) {
        System.out.print("추가할 기술 스택 이름 (예: Java, Git): ");
        String techName = scanner.nextLine();
        projectTechspecService.addTechspecToProject(currentProject, techName);
    }

    public void removeTechspecUI(Project currentProject) {
        projectTechspecService.viewProjectTechspecs(currentProject);
        System.out.println("----------------------------------------");
        System.out.print("삭제할 기술 스택의 번호(ID)를 입력하세요 (취소: b): ");
        String idInput = scanner.nextLine();

        if ("b".equalsIgnoreCase(idInput)) {
            return;
        }
        try {
            Long idToDelete = Long.parseLong(idInput);
            projectTechspecService.removeTechspecFromProject(currentProject, idToDelete);
        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 숫자를 입력해야 합니다.");
        }
    }

    public Set<String> inputTechSpecs() {
        Set<String> uniqueTechNames = new HashSet<>();
        System.out.println("\n---------- 요구 스택 입력 ----------");
        while (true) {
            System.out.print("추가할 기술 스택 이름 (완료: q): ");
            String techName = scanner.nextLine();

            if ("q".equalsIgnoreCase(techName)) {
                break;
            }
            uniqueTechNames.add(techName.toUpperCase());
        }
        return uniqueTechNames;
    }


}