package src.matching;

import src.member.Member;
import java.util.List;
import java.util.Scanner;

public class MatchingController {

    private final MatchingService matchingService;
    private final Scanner scanner;

    public MatchingController(MatchingService matchingService, Scanner scanner) {
        this.matchingService = matchingService;
        this.scanner = scanner;
    }

    public void showMatchingMenu(Member currentUser) {
        while (true) {
            System.out.println("\n========== 프로젝트 매칭 시스템 ==========");
            System.out.println("사용자: " + currentUser.getEmail());
            System.out.println("1. MBTI 기반 추천 (성향이 잘 맞는 팀)");
            System.out.println("2. TechSpec 기반 추천 (기술 스택이 맞는 팀)");
            System.out.println("3. 종합 추천 (MBTI + Tech 가중치)");
            System.out.println("b. 뒤로 가기");
            System.out.print("선택: ");

            String choice = scanner.nextLine();
            List<MatchedProject> results = null;

            switch (choice) {
                case "1":
                    System.out.println("\n[MBTI 기반 추천 결과 (상위 10개)]");
                    results = matchingService.getMbtiMatches(currentUser.getId());
                    break;
                case "2":
                    System.out.println("\n[TechSpec 기반 추천 결과 (상위 10개)]");
                    results = matchingService.getTechMatches(currentUser.getId());
                    break;
                case "3":
                    System.out.println("\n[종합 추천 결과 (상위 10개)]");
                    results = matchingService.getCombinedMatches(currentUser.getId());
                    break;
                case "b":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
                    continue; // 다시 루프 시작
            }

            // 결과 출력 공통 로직
            if (results != null) {
                if (results.isEmpty()) {
                    System.out.println("조건에 맞는 프로젝트를 찾지 못했습니다.");
                    System.out.println("(팁: 내 MBTI나 기술 스택을 먼저 등록했는지 확인해보세요!)");
                } else {
                    int rank = 1;
                    for (MatchedProject p : results) {
                        System.out.printf("%d. [%d] %s\n", rank++, p.getId(), p.getTitle());
                        System.out.printf("   └─ 점수: %d | %s\n", p.getScore(), p.getDescription());
                    }
                }

                System.out.println("\n(엔터키를 누르면 매칭 메뉴로 돌아갑니다)");
                scanner.nextLine();
            }
        }
    }
}