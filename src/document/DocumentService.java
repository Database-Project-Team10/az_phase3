package src.document;

import java.util.List;

public class DocumentService {

    private final DocumentRepository documentRepository = new DocumentRepository();

    public boolean createDocument(Document document) {
        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByProject(Long projectId) {
        return documentRepository.findByProjectId(projectId);
    }
    
    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId);
    }

    public boolean updateDocument(Document document) {
        return documentRepository.update(document);
    }

    public boolean deleteDocument(Long documentId) {
        return documentRepository.delete(documentId);
    }
}