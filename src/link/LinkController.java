package src.link;

import src.member.MemberService;
import java.util.List;
import java.util.Scanner;
import src.link.exception.LinkException;

public class LinkController {

    private final LinkService linkService = new LinkService();
    private final MemberService memberService = new MemberService();
    private final Scanner scanner = new Scanner(System.in);

    public void showLinkMenu(Long projectId) {
        while (true) {
            System.out.println("\n---------- 링크 기능 ----------");

            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("현재 접속 중인 프로젝트: " + projectId);
                System.out.println("1. 전체 링크 보기");
                System.out.println("2. 링크 작성");
                System.out.println("3. 링크 수정");
                System.out.println("4. 링크 삭제");
                System.out.println("b. 뒤로 가기");
            } else {
            	System.out.println("로그인이 필요합니다.");
                return;
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();
            Long linkId = null;
            String title = null;
            String url = null;

            switch (choice) {
                case "1":
                    List<Link> links = linkService.getLinksByProject(projectId);
                    showLinkList(links);
                    System.out.print("\n엔터키를 누르면 링크 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "2":
                    System.out.println("---------- 링크 작성 ----------");
                    try {
                        System.out.print("링크 제목: ");
                        title = scanner.nextLine();
                        System.out.print("링크 URL (https://...): ");
                        url = scanner.nextLine();

                        Link newLink = new Link(projectId, title, url);
                        
                        linkService.createLink(newLink);
                        
                        System.out.println("링크가 성공적으로 작성되었습니다."); // [!] 3. try 성공 시 메시지
                        
                    } catch (LinkException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    
                    System.out.print("\n엔터키를 누르면 링크 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "3":
                    System.out.println("---------- 링크 수정 ----------");
                    showLinkList(linkService.getLinksByProject(projectId));
                    System.out.print("수정할 링크의 번호를 입력하세요: ");
                    
                    try {
                        linkId = Long.parseLong(scanner.nextLine());
                        
                        Link targetLink = linkService.getLink(linkId);

                        String newTitle;
                        while (true) {
                            System.out.print("제목을 수정하시겠습니까? (Y/N, 현재: " + targetLink.getTitle() + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                System.out.print("수정할 제목 입력: ");
                                newTitle = scanner.nextLine();
                                break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                newTitle = targetLink.getTitle(); // 기존 제목 유지
                                break;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }

                        String newUrl;
                        while (true) {
                            System.out.print("URL을 수정하시겠습니까? (Y/N, 현재: " + targetLink.getUrl() + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                System.out.print("수정할 URL 입력: ");
                                newUrl = scanner.nextLine();
                                break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                newUrl = targetLink.getUrl();
                                break;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }
                        
                        Link updatedLink = new Link(
                                targetLink.getId(), 
                                targetLink.getProjectId(), 
                                newTitle, 
                                newUrl
                            );
                        
                        linkService.updateLink(updatedLink, projectId);
                        
                        System.out.println("링크가 성공적으로 수정되었습니다.");

                    } catch (NumberFormatException e) {
                        System.out.println("오류: 유효한 ID 번호를 입력하세요.");
                    } catch (LinkException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    System.out.print("\n엔터키를 누르면 링크 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "4":
                    System.out.println("---------- 링크 삭제 ----------");
                    showLinkList(linkService.getLinksByProject(projectId));
                    System.out.print("삭제할 링크의 ID 번호를 입력하세요: ");

                    try {
                        linkId = Long.parseLong(scanner.nextLine());
                        
                        linkService.deleteLink(linkId, projectId);
                        
                        System.out.println("링크가 성공적으로 삭제되었습니다.");
                        
                    } catch (NumberFormatException e) {
                        System.out.println("오류: 유효한 ID 번호를 입력하세요.");
                    } catch (LinkException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    System.out.print("\n엔터키를 누르면 링크 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
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