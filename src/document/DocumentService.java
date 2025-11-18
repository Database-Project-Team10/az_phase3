package src.document;

import java.util.List;
import src.document.dto.DocumentRequestDto;
import src.document.exception.DocumentAccessException;
import src.document.exception.DocumentNotFoundException;
import src.document.exception.InvalidDocumentInputException;
public class DocumentService {

    private final DocumentRepository documentRepository = new DocumentRepository();
    
    private void validateLocation(String location) {
        if (!location.matches("^/.+")) {
            throw new InvalidDocumentInputException("문서 위치 형식이 올바르지 않습니다. '/docs/file.pdf'처럼 슬래시(/)로 시작하는 경로여야 합니다.");
        }
    }
    
    public Document createDocument(Long projectId, DocumentRequestDto requestDto) {
        if (requestDto.getTitle().trim().isEmpty() || requestDto.getLocation().trim().isEmpty()) {
            throw new InvalidDocumentInputException("제목과 위치는 비워둘 수 없습니다.");
        }

        validateLocation(requestDto.getLocation());

        Document document = new Document(projectId, requestDto.getTitle(), requestDto.getLocation());
        return documentRepository.save(document);
    }
    
    public List<Document> getDocumentsByProject(Long projectId) {
        return documentRepository.findByProjectId(projectId);
    }
    
    public Document getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId);
        if (document == null) {
            throw new DocumentNotFoundException();
        }
        return document;
    }

    public void updateDocument(Long documentId, Long projectId, DocumentRequestDto requestDto) {
        Document targetDocument = documentRepository.findById(documentId);

        if (targetDocument == null) {
            throw new DocumentNotFoundException();
        }

        if (requestDto.getTitle().trim().isEmpty() || requestDto.getLocation().trim().isEmpty()) {
            throw new InvalidDocumentInputException("제목과 위치는 비워둘 수 없습니다.");
        }

        if (!targetDocument.getProjectId().equals(projectId)) {
            throw new DocumentAccessException("해당 문서는 이 프로젝트에 속하지 않아 수정할 수 없습니다.");
        }

        validateLocation(requestDto.getLocation());

        Document document = new Document(documentId, projectId, requestDto.getTitle(), requestDto.getLocation());
        documentRepository.update(document);
    }
    
    public void deleteDocument(Long documentId, Long expectedProjectId) {
        Document targetDocument = documentRepository.findById(documentId);
        
        if (targetDocument == null) {
            throw new DocumentNotFoundException();
        }
        
        if (!targetDocument.getProjectId().equals(expectedProjectId)) {
            throw new DocumentAccessException("해당 문서는 이 프로젝트에 속하지 않아 삭제할 수 없습니다.");
        }

        documentRepository.delete(documentId);
    }
}