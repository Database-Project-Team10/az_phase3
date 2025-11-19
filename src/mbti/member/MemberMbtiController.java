package src.mbti.member;

import src.mbti.MbtiDimension;
import src.mbti.exception.InvalidMbtiException;
import src.mbti.exception.MbtiException;
import src.mbti.exception.MbtiNotFoundException;
import src.mbti.project.ProjectMbtiService;
import src.utils.InputUtil;
import src.member.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemberMbtiController {

    private final MemberMbtiService memberMbtiService;
    private final Scanner scanner;

    public MemberMbtiController(
            MemberMbtiService memberMbtiService,
            Scanner scanner
    ) {
        this.memberMbtiService = memberMbtiService;
        this.scanner = scanner;
    }

    public void showMemberMbtiMenu(Member currentUser) {
        System.out.println("---------- 내 MBTI 입력/수정 ----------");

        try {
            // 1. 차원 불러오기
            List<MbtiDimension> dimensions = memberMbtiService.getMbtiDimensions();
            if (dimensions == null || dimensions.isEmpty()) {
                System.out.println("오류: MBTI 데이터를 불러올 수 없습니다.");
                return;
            }
            printCurrentMbti(currentUser, dimensions);

            // 2. 사용자 입력 받기
            Map<Long, String> newMbtiMap = getUserMbtiInput(dimensions);

            // 3. 저장 요청
            memberMbtiService.saveMyMbti(currentUser.getId(), newMbtiMap);
            System.out.println("MBTI 정보가 성공적으로 저장되었습니다.");

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 입력이 취소되었습니다.");
        } catch (MbtiException e) {
            System.out.println("[오류]: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[오류]:알 수 없는 오류가 발생했습니다.");
        }
    }

    private void printCurrentMbti(Member user, List<MbtiDimension> dimensions) {
        Map<Long, String> currentMbti = memberMbtiService.getMbtiMapByMemberId(user.getId());

        System.out.print("현재 설정된 MBTI: ");
        for (MbtiDimension dim : dimensions) {
            System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
        }
        System.out.println("\n(4가지 차원을 모두 입력합니다.)");
    }


    private Map<Long, String> getUserMbtiInput(List<MbtiDimension> dimensions) {
        Map<Long, String> newMbtiMap = new HashMap<>();

        for (MbtiDimension dim : dimensions) {
            String input;

            String prompt = String.format("%d. %s (%s/%s)",
                    dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());

            while (true) {
                input = InputUtil.getInput(scanner, prompt).toUpperCase();

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
}