package src.mbti.project;

import src.mbti.MbtiDimension;
import src.mbti.exception.InvalidMbtiException;
import src.mbti.exception.MbtiException;
import src.mbti.exception.MbtiNotFoundException;
import src.project.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectMbtiController {

    private final ProjectMbtiService projectMbtiService;
    private final Scanner scanner;

    public ProjectMbtiController(
            ProjectMbtiService projectMbtiService,
            Scanner scanner
    ) {
        this.projectMbtiService = projectMbtiService;
        this.scanner = scanner;
    }

    public void showProjectMbtiMenu(Project currentProject) {

        while (true) {
            System.out.println("\n---------- [" + currentProject.getTitle() + "] 희망 MBTI 관리 ----------");

            try {
                List<MbtiDimension> dimensions = projectMbtiService.getMbtiDimensions();
                Map<Long, String> currentMbti = projectMbtiService.getMbtiMapByProjectId(currentProject.getId());

                printCurrentMbti(dimensions, currentMbti);

                System.out.println("1. 희망 MBTI 수정");
                System.out.println("b. 뒤로 가기 (상세 메뉴)");
                System.out.print("메뉴를 선택하세요: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        Map<Long, String> newMbtiMap = promptMbtiDimensions(dimensions);
                        projectMbtiService.saveProjectMbti(currentProject.getId(), newMbtiMap);
                        System.out.println("프로젝트 선호 MBTI가 성공적으로 저장되었습니다.");
                        break;

                    case "b":
                        return;

                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } catch (MbtiException e) {
                System.out.println("MBTI 처리 중 오류가 발생했습니다: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("알 수 없는 오류가 발생했습니다.");
            }
        }
    }

    private void printCurrentMbti(List<MbtiDimension> dimensions,
                                  Map<Long, String> currentMbti) {
        System.out.print("현재 설정된 선호 MBTI: ");
        for (MbtiDimension dim : dimensions) {
            System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
        }
        System.out.println();
    }


    private Map<Long, String> promptMbtiDimensions(List<MbtiDimension> dimensions) {
        Map<Long, String> newMbtiMap = new HashMap<>();

        for (MbtiDimension dim : dimensions) {
            String input;

            while (true) {
                System.out.printf("%d. %s (%s/%s): ",
                        dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());

                input = scanner.nextLine().toUpperCase();

                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input);
                    break;
                } else {
                    System.out.printf("잘못된 입력입니다. %s 또는 %s를 입력해주세요.\n",
                            dim.getOption1(), dim.getOption2());
                }
            }
        }

        return newMbtiMap;
    }

    public Map<Long, String> inputMbti() {
        try {
            List<MbtiDimension> dimensions = projectMbtiService.getMbtiDimensions();
            System.out.println("\n---------- 선호 MBTI 입력 ----------");
            System.out.println("(4가지 차원을 모두 입력합니다.)");

            return promptMbtiDimensions(dimensions);

        } catch (MbtiException e) {
            System.out.println("MBTI 데이터 오류: " + e.getMessage());
            return new HashMap<>();
        }
    }
}