package src.mbti.project;

import src.mbti.MbtiDimension;
import src.project.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectMbtiController {

    private final ProjectMbtiService projectMbtiService = new ProjectMbtiService();
    private final Scanner scanner = new Scanner(System.in);

    public void showProjectMbtiMenu(Project currentProject) {

        while (true) {
            System.out.println("\n---------- [" + currentProject.getTitle() + "] 희망 MBTI 관리 ----------");
            List<MbtiDimension> dimensions = projectMbtiService.getMbtiDimensions();
            if (dimensions == null || dimensions.isEmpty()) {
                System.out.println("오류: MBTI 마스터 데이터를 불러올 수 없습니다.");
                return;
            }
            Map<Long, String> currentMbti = projectMbtiService.getMbtiMapByProjectId(currentProject.getId());
            System.out.print("현재 설정된 선호 MBTI: ");
            for (MbtiDimension dim : dimensions) {
                System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
            }
            System.out.println();
            System.out.println("1. 희망 MBTI 수정");
            System.out.println("b. 뒤로 가기 (상세 메뉴)");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();


            switch (choice) {
                case "1":
                    System.out.println("\n(4가지 차원을 모두 입력합니다.)");

                    // [수정] 헬퍼 메서드 호출로 변경
                    Map<Long, String> newMbtiMap = promptMbtiDimensions(dimensions);

                    boolean isSuccess = projectMbtiService.saveProjectMbti(currentProject.getId(), newMbtiMap);

                    if (isSuccess) {
                        System.out.println("프로젝트 선호 MBTI가 성공적으로 저장되었습니다.");
                    } else {
                        System.out.println("오류: MBTI 정보 저장에 실패했습니다.");
                    }
                    break;
                case "b":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private Map<Long, String> promptMbtiDimensions(List<MbtiDimension> dimensions) {
        Map<Long, String> newMbtiMap = new HashMap<>();

        for (MbtiDimension dim : dimensions) {
            String input = "";
            while (true) {
                // 사용자에게 더 친절한 상세 프롬프트 (ID 포함)
                System.out.printf("%d. %s (%s/%s): ", dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                input = scanner.nextLine().toUpperCase();

                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input);
                    break;
                } else {
                    // 사용자에게 더 친절한 상세 에러 메시지
                    System.out.printf("잘못된 입력입니다. %s 또는 %s를 입력해주세요.\n", dim.getOption1(), dim.getOption2());
                }
            }
        }
        return newMbtiMap;
    }

    public Map<Long, String> inputMbti() {
        List<MbtiDimension> dimensions = projectMbtiService.getMbtiDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            System.out.println("오류: MBTI 데이터를 불러올 수 없습니다.");
            return new HashMap<>(); // 빈 맵 반환
        }

        System.out.println("\n---------- 선호 MBTI 입력 ----------");
        System.out.println("(4가지 차원을 모두 입력합니다.)");

        // [수정] 헬퍼 메서드를 호출하고 그 결과를 바로 반환
        return promptMbtiDimensions(dimensions);
    }
}