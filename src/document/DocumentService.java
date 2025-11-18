package src.document;

import src.document.exception.DocumentAccessException;
import src.document.exception.InvalidDocumentInputException;

import java.util.List;

public class DocumentService {

    private final DocumentRepository documentRepository;
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    private void validateLocation(String location) {
        if (!location.matches("^/.+")) {
            throw new InvalidDocumentInputException("문서 위치 형식이 올바르지 않습니다. '/docs/file.pdf'처럼 슬래시(/)로 시작하는 경로여야 합니다.");
        }
    }
    
    public boolean createDocument(Document document) {
        validateLocation(document.getLocation()); // [!] 2. 저장 전 검증
        return documentRepository.save(document);
    }
    
    public List<Document> getDocumentsByProject(Long projectId) {
        return documentRepository.findByProjectId(projectId);
    }
    
    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId);
    }

    public boolean updateDocument(Document updatedDocument, Long expectedProjectId) {
        Document targetDocument = documentRepository.findById(updatedDocument.getId());
        if (targetDocument == null || !targetDocument.getProjectId().equals(expectedProjectId)) {
            throw new DocumentAccessException("수정 권한이 없거나 유효하지 않은 문서 ID입니다.");
        }
        validateLocation(updatedDocument.getLocation());
        return documentRepository.update(updatedDocument);
    }

    public boolean deleteDocument(Long documentId, Long expectedProjectId) {
        Document targetDocument = documentRepository.findById(documentId);
        if (targetDocument == null || !targetDocument.getProjectId().equals(expectedProjectId)) {
            throw new DocumentAccessException("삭제 권한이 없거나 유효하지 않은 문서 ID입니다.");
        }
        return documentRepository.delete(documentId);
    }
}