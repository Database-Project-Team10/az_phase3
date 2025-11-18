package src.document;

import src.document.exception.DocumentException;
import src.document.exception.InvalidDocumentInputException;
import src.document.dto.DocumentRequestDto;
import src.document.exception.DocumentException;
import src.member.MemberService;

import java.util.List;
import java.util.Scanner;

public class DocumentController {

    private final DocumentService documentService;
    private final MemberService memberService;
    private final Scanner scanner;

    public DocumentController(
            DocumentService documentService,
            MemberService memberService,
            Scanner scanner
    ) {
        this.documentService = documentService;
        this.memberService = memberService;
        this.scanner = scanner;
    }

    public void showDocumentMenu(Long projectId) {
        while (true) {
            printMenuHeader(projectId);

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인이 필요합니다.");
                return;
            }

            String choice = getMenuChoice();

            switch (choice) {
                case "1":
                    handleShowAllDocuments(projectId);
                    break;

                case "2":
                    handleCreateDocument(projectId);
                    break;

                case "3":
                    handleUpdateDocument(projectId);
                    break;

                case "4":
                    handleDeleteDocument(projectId);
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printMenuHeader(Long projectId) {
        System.out.println("\n---------- 문서 기능 ----------");
        System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
        System.out.println("현재 접속 중인 프로젝트: " + projectId);
        System.out.println("1. 전체 문서 보기");
        System.out.println("2. 문서 등록");
        System.out.println("3. 문서 수정");
        System.out.println("4. 문서 삭제");
        System.out.println("b. 뒤로 가기");
    }

    private String getMenuChoice() {
        System.out.print("메뉴를 선택하세요: ");
        return scanner.nextLine();
    }

    private void handleShowAllDocuments(Long projectId) {
        try {
            List<Document> documents = documentService.getDocumentsByProject(projectId);
            showDocumentList(documents);
        } catch (DocumentException e) {
            printError(e);
        }
        pause();
    }

    private void handleCreateDocument(Long projectId) {
        System.out.println("---------- 문서 등록 ----------");

        try {
            System.out.print("문서 제목: ");
            String title = scanner.nextLine();

            System.out.print("문서 위치 (e.g., /docs/file.pdf): ");
            String location = scanner.nextLine();

            DocumentRequestDto requestDto = new DocumentRequestDto(title, location);

            documentService.createDocument(projectId, requestDto);

            System.out.println("문서가 성공적으로 작성되었습니다.");

        } catch (DocumentException e) {
            printError(e);
        }
        pause();
    }

    private void handleUpdateDocument(Long projectId) {
        System.out.println("---------- 문서 수정 ----------");
        showDocumentList(documentService.getDocumentsByProject(projectId));

        try {
            System.out.print("수정할 문서의 번호를 입력하세요: ");
            Long documentId = Long.parseLong(scanner.nextLine());

            Document targetDocument = documentService.getDocument(documentId);

            String newTitle = promptTitleUpdate(targetDocument);
            String newLocation = promptLocationUpdate(targetDocument);

            DocumentRequestDto requestDto = new DocumentRequestDto(newTitle, newLocation);

            documentService.updateDocument(targetDocument.getId(), projectId, requestDto);
            System.out.println("문서가 성공적으로 수정되었습니다.");

        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 ID 번호를 입력하세요.");
        } catch (DocumentException e) {
            printError(e);
        }
        pause();
    }

    private String promptTitleUpdate(Document document) {
        while (true) {
            System.out.print("제목을 수정하시겠습니까? (Y/N): ");
            String c = scanner.nextLine();

            if (c.equalsIgnoreCase("Y")) {
                System.out.print("수정할 제목 입력: ");
                return scanner.nextLine();
            } else if (c.equalsIgnoreCase("N")) {
                return document.getTitle();
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private String promptLocationUpdate(Document document) {
        while (true) {
            System.out.print("위치를 수정하시겠습니까? (Y/N): ");
            String c = scanner.nextLine();

            if (c.equalsIgnoreCase("Y")) {
                System.out.print("수정할 위치 입력: ");
                return scanner.nextLine();
            } else if (c.equalsIgnoreCase("N")) {
                return document.getLocation();
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void handleDeleteDocument(Long projectId) {
        System.out.println("---------- 문서 삭제 ----------");
        showDocumentList(documentService.getDocumentsByProject(projectId));

        try {
            System.out.print("삭제할 문서의 ID 번호를 입력하세요: ");
            Long documentId = Long.parseLong(scanner.nextLine());

            documentService.deleteDocument(documentId, projectId);
            System.out.println("문서가 성공적으로 삭제되었습니다.");

        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 ID 번호를 입력하세요.");
        } catch (DocumentException e) {
            printError(e);
        }
        pause();
    }

    private void printError(Exception e) {
        System.out.println("[오류] " + e.getMessage());
    }

    private void pause() {
        System.out.print("\n엔터키를 누르면 문서 메뉴로 돌아갑니다.");
        scanner.nextLine();
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