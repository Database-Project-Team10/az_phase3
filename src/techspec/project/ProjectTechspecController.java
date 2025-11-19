package src.techspec.project;

import src.techspec.Techspec;
import src.techspec.exception.TechspecException;
import src.project.Project;
import src.utils.InputUtil;

import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

public class ProjectTechspecController {

    private final ProjectTechspecService projectTechspecService;
    private final Scanner scanner;

    public ProjectTechspecController(
            ProjectTechspecService projectTechspecService,
            Scanner scanner
    ) {
        this.projectTechspecService = projectTechspecService;
        this.scanner = scanner;
    }

    public void showProjectTechspecs(Project currentProject) {
        System.out.println("\n---------- [" + currentProject.getTitle() + "] 요구 스택 목록 ----------");
        for (Techspec t : projectTechspecService.getProjectTechspecs(currentProject)) {
            System.out.println(t.getId() + ". " + t.getName());
        }
    }

    public void showProjectTechspecMenu(Project currentProject) {
        while (true) {
            System.out.println("\n---------- [" + currentProject.getTitle() + "] 요구 테크스펙 관리 ----------");
            System.out.println("1. 요구 테크스펙 목록 보기");
            System.out.println("2. 테크스펙 추가");
            System.out.println("3. 테크스펙 삭제");
            System.out.println("b. 뒤로 가기 (상세 메뉴)");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        showProjectTechspecs(currentProject);
                        break;
                    case "2":
                        addTechspecUI(currentProject);
                        break;
                    case "3":
                        removeTechspecUI(currentProject);
                        break;
                    case "b":
                        return;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } catch (InputUtil.CancelException e) {
                System.out.println("\n[!] 작업이 취소되었습니다.");
            }
        }
    }

    private void addTechspecUI(Project currentProject) {
        String techName = InputUtil.getInput(scanner, "추가할 기술 스택 이름 (예: Java, Git)");

        try {
            projectTechspecService.addTechspecToProject(currentProject, techName);
            System.out.println("'" + techName + "' 스택이 프로젝트에 추가되었습니다.");
        } catch (TechspecException e) {
            System.out.println("[오류]: " + e.getMessage());
        }
    }

    private void removeTechspecUI(Project currentProject) {
        showProjectTechspecs(currentProject);

        Long techId = InputUtil.getLong(scanner, "삭제할 기술 스택의 번호(ID)");

        try {
            projectTechspecService.removeTechspecFromProject(currentProject, techId);
            System.out.println("스택이 성공적으로 삭제되었습니다.");
        } catch (TechspecException e) {
            System.out.println("[오류]: " + e.getMessage());
        }
    }

    public Set<String> inputTechSpecs() {
        Set<String> uniqueTechNames = new HashSet<>();
        System.out.println("\n---------- 요구 스택 입력 ----------");
        while (true) {
            System.out.print("추가할 기술 스택 이름 (완료: q): ");
            String techName = scanner.nextLine();

            if ("q".equalsIgnoreCase(techName)) break;
            uniqueTechNames.add(techName.toUpperCase());
        }
        return uniqueTechNames;
    }
}