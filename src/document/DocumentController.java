package src.document;

import src.member.MemberService;
import java.util.List;
import java.util.Scanner;
import src.document.exception.DocumentException;
import src.document.exception.InvalidDocumentInputException;

public class DocumentController {

    private final DocumentService documentService = new DocumentService();
    private final MemberService memberService = new MemberService();
    private final Scanner scanner = new Scanner(System.in);

    public DocumentController() {
    }

    public void showDocumentMenu(Long projectId) {
        while (true) {
            System.out.println("\n---------- 문서 기능 ----------");

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인이 필요합니다.");
                return;
            }
            
            System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
            System.out.println("현재 접속 중인 프로젝트: " + projectId);
            System.out.println("1. 전체 문서 보기");
            System.out.println("2. 문서 작성");
            System.out.println("3. 문서 수정");
            System.out.println("4. 문서 삭제");
            System.out.println("b. 뒤로 가기");

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();
            Long documentId = null;
            String title = null;
            String location = null;

            switch (choice) {
                case "1": // 문서 목록
                    List<Document> documents = documentService.getDocumentsByProject(projectId);
                    showDocumentList(documents);
                    System.out.print("\n엔터키를 누르면 문서 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "2":
                    System.out.println("---------- 문서 작성 ----------");

                    try {
                        System.out.print("문서 제목: ");
                        title = scanner.nextLine();
                        System.out.print("문서 위치 (e.g., /docs/file.pdf): ");
                        location = scanner.nextLine();

                        if (title.trim().isEmpty() || location.trim().isEmpty()) {
                            throw new InvalidDocumentInputException("제목과 위치는 비워둘 수 없습니다.");
                        }
                        
                        Document newDocument = new Document(projectId, title, location);
                        
                        if (documentService.createDocument(newDocument)) {
                            System.out.println("문서가 성공적으로 작성되었습니다.");
                        } else {
                            System.out.println("문서 작성에 실패했습니다.");
                        }
                    } catch (DocumentException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    
                    System.out.print("\n엔터키를 누르면 문서 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "3":
                    System.out.println("---------- 문서 수정 ----------");
                    showDocumentList(documentService.getDocumentsByProject(projectId));
                    System.out.print("수정할 문서의 번호를 입력하세요: ");
                    
                    try {
                        documentId = Long.parseLong(scanner.nextLine());
                        Document targetDocument = documentService.getDocument(documentId);

                        if (targetDocument == null) {
                            System.out.println("오류: 해당 문서를 찾을 수 없습니다.");
                            break;
                        }

                        String newTitle;
                        while (true) {
                            System.out.print("제목을 수정하시겠습니까? (Y/N, 현재: " + targetDocument.getTitle() + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                System.out.print("수정할 제목 입력: ");
                                newTitle = scanner.nextLine();
                                break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                newTitle = targetDocument.getTitle();
                                break;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }

                        String newLocation;
                        while (true) {
                            System.out.print("위치를 수정하시겠습니까? (Y/N, 현재: " + targetDocument.getLocation() + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                System.out.print("수정할 위치 입력: ");
                                newLocation = scanner.nextLine();
                                break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                newLocation = targetDocument.getLocation();
                                break;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }

                        if (newTitle.trim().isEmpty() || newLocation.trim().isEmpty()) {
                            throw new InvalidDocumentInputException("제목과 위치는 비워둘 수 없습니다.");
                        }

                        Document updatedDocument = new Document(
                            targetDocument.getId(), 
                            targetDocument.getProjectId(), 
                            newTitle, 
                            newLocation
                        );
                        
                        if (documentService.updateDocument(updatedDocument, projectId)) {
                            System.out.println("문서가 성공적으로 수정되었습니다.");
                        } else {
                            System.out.println("문서 수정에 실패했습니다.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("오류: 유효한 ID 번호를 입력하세요.");
                    } catch (DocumentException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    System.out.print("\n엔터키를 누르면 문서 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "4":
                    System.out.println("---------- 문서 삭제 ----------");
                    showDocumentList(documentService.getDocumentsByProject(projectId));
                    System.out.print("삭제할 문서의 ID 번호를 입력하세요: ");

                    try {
                        documentId = Long.parseLong(scanner.nextLine());
                        
                        if (documentService.deleteDocument(documentId, projectId)) {
                            System.out.println("문서가 성공적으로 삭제되었습니다.");
                        } else {
                            System.out.println("문서 삭제에 실패했습니다.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("오류: 유효한 ID 번호를 입력하세요.");
                    } catch (DocumentException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    System.out.print("\n엔터키를 누르면 문서 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void showDocumentList(List<Document> documentList) {
        System.out.println("---------- 문서 목록 ----------");
        if (documentList == null || documentList.isEmpty()) {
            System.out.println("(등록된 문서가 없습니다.)");
            return;
        }
        
        for (Document document : documentList) {
            System.out.println(document.getId() + ". " + document.getTitle());
            System.out.println("   Location: " + document.getLocation());
            System.out.println();
        }
    }
}