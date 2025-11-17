package src.mbti.member;

import src.mbti.MbtiDimension;
import src.member.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemberMbtiController {

    private final MemberMbtiService memberMbtiService = new MemberMbtiService();
    private final Scanner scanner = new Scanner(System.in);

    public void showMemberMbtiMenu(Member currentUser) {
        System.out.println("---------- 내 MBTI 입력/수정 ----------");

        // 1. (Service 호출) DB에서 MBTI 4가지 차원 정의를 가져옴
        List<MbtiDimension> dimensions = memberMbtiService.getMbtiDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            System.out.println("오류: MBTI 마스터 데이터를 불러올 수 없습니다.");
            return;
        }

        // 2. (Service 호출) 현재 설정된 MBTI 값을 가져옴
        Map<Long, String> currentMbti = memberMbtiService.getMbtiMapByMemberId(currentUser.getId());
        System.out.print("현재 설정된 MBTI: ");
        for (MbtiDimension dim : dimensions) {
            System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
        }
        System.out.println("\n(4가지 차원을 모두 입력합니다.)");


        // 3. (Controller) 사용자에게 4가지 차원 "입력받기"
        Map<Long, String> newMbtiMap = new HashMap<>();
        for (MbtiDimension dim : dimensions) {
            String input = "";
            while (true) {
                System.out.printf("%d. %s (%s/%s): ", dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                input = scanner.nextLine().toUpperCase(); // 대문자로 변경

                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input); // Map에 저장 (key: mbti_id, value: 'E')
                    break;
                } else {
                    System.out.printf("잘못된 입력입니다. %s 또는 %s를 입력해주세요.\n", dim.getOption1(), dim.getOption2());
                }
            }
        }

        // 4. (Service 호출) DB에 저장 요청
        boolean isSuccess = memberMbtiService.saveMyMbti(currentUser.getId(), newMbtiMap);

        if (isSuccess) {
            System.out.println("MBTI 정보가 성공적으로 저장되었습니다.");
        } else {
            System.out.println("오류: MBTI 정보 저장에 실패했습니다.");
        }
    }
}
