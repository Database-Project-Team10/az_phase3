package src.document;

public class Document {
    Long id;          // PK (document_id)
    Long projectId;   // FK (project_id)
    String title;
    String location;


    public Document(Long projectId, String title, String location) {
        this.projectId = projectId;
        this.title = title;
        this.location = location;
    }

    public Document(Long id, Long projectId, String title, String location) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.location = location;
    }


    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }
}
