package src.link;

import src.link.dto.LinkRequestDto;
import src.link.exception.LinkException;
import src.member.MemberService;

import java.util.List;
import java.util.Scanner;

public class LinkController {

    private final LinkService linkService;
    private final MemberService memberService;
    private final Scanner scanner;

    public LinkController(
            LinkService linkService,
            MemberService memberService,
            Scanner scanner
    ) {
        this.linkService = linkService;
        this.memberService = memberService;
        this.scanner = scanner;
    }

    public void showLinkMenu(Long projectId) {
        while (true) {

            printMenuHeader(projectId);

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인이 필요합니다.");
                return;
            }

            String choice = getMenuChoice();

            switch (choice) {
                case "1":
                    handleShowAllLinks(projectId);
                    break;

                case "2":
                    handleCreateLink(projectId);
                    break;

                case "3":
                    handleUpdateLink(projectId);
                    break;

                case "4":
                    handleDeleteLink(projectId);
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printMenuHeader(Long projectId) {
        System.out.println("\n---------- 링크 기능 ----------");
        System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
        System.out.println("현재 접속 중인 프로젝트: " + projectId);
        System.out.println("1. 전체 링크 보기");
        System.out.println("2. 링크 등록");
        System.out.println("3. 링크 수정");
        System.out.println("4. 링크 삭제");
        System.out.println("b. 뒤로 가기");
    }

    private String getMenuChoice() {
        System.out.print("메뉴를 선택하세요: ");
        return scanner.nextLine();
    }

    private void handleShowAllLinks(Long projectId) {
        try {
            List<Link> links = linkService.getLinksByProject(projectId);
            showLinkList(links);
        } catch (LinkException e) {
            printError(e);
        }
        pause();
    }

    private void handleCreateLink(Long projectId) {
        System.out.println("---------- 링크 등록 ----------");

        try {
            System.out.print("링크 제목: ");
            String title = scanner.nextLine();

            System.out.print("링크 URL (https://...): ");
            String url = scanner.nextLine();

            LinkRequestDto linkRequestDto = new LinkRequestDto(title, url);

            linkService.createLink(projectId, linkRequestDto);

            System.out.println("링크가 성공적으로 작성되었습니다.");

        } catch (LinkException e) {
            printError(e);
        }

        pause();
    }

    private void handleUpdateLink(Long projectId) {
        System.out.println("---------- 링크 수정 ----------");
        showLinkList(linkService.getLinksByProject(projectId));

        try {
            System.out.print("수정할 링크의 번호를 입력하세요: ");
            Long linkId = Long.parseLong(scanner.nextLine());

            Link targetLink = linkService.getLink(linkId);

            String newTitle = promptTitleUpdate(targetLink);
            String newUrl = promptUrlUpdate(targetLink);

            LinkRequestDto linkRequestDto = new LinkRequestDto(newTitle, newUrl);

            linkService.updateLink(targetLink.getId(), projectId, linkRequestDto);
            System.out.println("링크가 성공적으로 수정되었습니다.");

        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 ID 번호를 입력하세요.");
        } catch (LinkException e) {
            printError(e);
        }

        pause();
    }

    private String promptTitleUpdate(Link link) {
        while (true) {
            System.out.print("제목을 수정하시겠습니까? (Y/N): ");
            String c = scanner.nextLine();

            if (c.equalsIgnoreCase("Y")) {
                System.out.print("수정할 제목 입력: ");
                return scanner.nextLine();
            } else if (c.equalsIgnoreCase("N")) {
                return link.getTitle();
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private String promptUrlUpdate(Link link) {
        while (true) {
            System.out.print("URL을 수정하시겠습니까? (Y/N): ");
            String c = scanner.nextLine();

            if (c.equalsIgnoreCase("Y")) {
                System.out.print("수정할 URL 입력: ");
                return scanner.nextLine();
            } else if (c.equalsIgnoreCase("N")) {
                return link.getUrl();
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }


    private void handleDeleteLink(Long projectId) {
        System.out.println("---------- 링크 삭제 ----------");

        showLinkList(linkService.getLinksByProject(projectId));

        try {
            System.out.print("삭제할 링크의 ID 번호를 입력하세요: ");
            Long linkId = Long.parseLong(scanner.nextLine());

            linkService.deleteLink(linkId, projectId);

            System.out.println("링크가 성공적으로 삭제되었습니다.");

        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 ID 번호를 입력하세요.");
        } catch (LinkException e) {
            printError(e);
        }

        pause();
    }


    private void printError(Exception e) {
        System.out.println("[오류] " + e.getMessage());
    }

    private void pause() {
        System.out.print("\n엔터키를 누르면 링크 메뉴로 돌아갑니다.");
        scanner.nextLine();
    }

    private void showLinkList(List<Link> linkList) {
        System.out.println("---------- 링크 목록 ----------");

        if (linkList == null || linkList.isEmpty()) {
            System.out.println("(등록된 링크가 없습니다.)");
            return;
        }

        for (Link link : linkList) {
            System.out.println(link.getId() + ". " + link.getTitle());
            System.out.println("   URL: " + link.getUrl());
            System.out.println();
        }
    }
}