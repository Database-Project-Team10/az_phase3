package src.mbti;

import src.project.Project;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectMbtiController {

    private final ProjectMbtiService projectMbtiService = new ProjectMbtiService();
    private final Scanner scanner = new Scanner(System.in);

    public void manageProjectMbti(Project currentProject) {
        System.out.println("\n---------- [" + currentProject.getTitle() + "] 선호 MBTI 입력/수정 ----------");

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
        System.out.println("\n(4가지 차원을 모두 입력합니다.)");

        Map<Long, String> newMbtiMap = new HashMap<>();
        for (MbtiDimension dim : dimensions) {
            String input = "";
            while (true) {
                System.out.printf("%d. %s (%s/%s): ", dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                input = scanner.nextLine().toUpperCase();

                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input);
                    break;
                } else {
                    System.out.printf("잘못된 입력입니다. %s 또는 %s를 입력해주세요.\n", dim.getOption1(), dim.getOption2());
                }
            }
        }

        boolean isSuccess = projectMbtiService.saveProjectMbti(currentProject.getId(), newMbtiMap);

        if (isSuccess) {
            System.out.println("프로젝트 선호 MBTI가 성공적으로 저장되었습니다.");
        } else {
            System.out.println("오류: MBTI 정보 저장에 실패했습니다.");
        }
    }
}