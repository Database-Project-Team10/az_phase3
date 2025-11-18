package src.techspec.member;

import src.member.Member;
import src.techspec.Techspec;
import src.techspec.dto.TechspecAddRequestDto;
import src.techspec.exception.TechspecException;
import src.techspec.project.ProjectTechspecService;

import java.util.Scanner;

public class MemberTechspecController {

    private final MemberTechspecService memberTechspecService;
    private final Scanner scanner;

    public MemberTechspecController(
            MemberTechspecService memberTechspecService,
            Scanner scanner
    ) {
        this.memberTechspecService = memberTechspecService;
        this.scanner = scanner;
    }

    public void showMemberTechspecMenu(Member currentUser) {

        while (true) {
            System.out.println("\n---------- 내 테크스펙 관리 ----------");
            System.out.println("현재 로그인: " + currentUser.getEmail());
            System.out.println("1. 내 스택 목록 보기");
            System.out.println("2. 스택 추가");
            System.out.println("3. 스택 삭제");
            System.out.println("b. 뒤로 가기 (회원 메뉴)");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        showMyTechspecs(currentUser);
                        break;

                    case "2":
                        System.out.print("추가할 기술 스택 이름 (예: Java, Git): ");
                        String techName = scanner.nextLine();
                        TechspecAddRequestDto techspecAddRequestDto = new TechspecAddRequestDto(techName);
                        memberTechspecService.addTechspec(currentUser, techspecAddRequestDto);
                        System.out.println("'" + techspecAddRequestDto.getName() + "' 스택이 성공적으로 추가되었습니다.");
                        break;

                    case "3":
                        showMyTechspecs(currentUser);
                        System.out.println("----------------------------------------");
                        System.out.print("삭제할 기술 스택의 번호(ID)를 입력하세요 (취소: b): ");
                        String idInput = scanner.nextLine();

                        if ("b".equalsIgnoreCase(idInput)) {
                            System.out.println("삭제를 취소했습니다.");
                            break;
                        }

                        Long idToDelete = Long.parseLong(idInput);
                        memberTechspecService.removeTechspec(currentUser, idToDelete);
                        System.out.println("삭제 성공!");
                        break;

                    case "b":
                        return;

                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } catch (TechspecException e) {
                System.out.println("[오류]: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[오류]: 숫자를 입력해주세요.");
            }
        }
    }

    private void showMyTechspecs(Member currentUser) {
        System.out.println("---------- 내 스택 목록 ----------");
        for (Techspec tech : memberTechspecService.getMyTechspecs(currentUser)) {
            System.out.println(tech.getId() + ". " + tech.getName());
        }
    }
}