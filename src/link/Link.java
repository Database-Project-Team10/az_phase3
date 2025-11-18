package src.link;


public class Link {
    private final Long id;
    private final Long projectId;
    private final String title;
    private final String url;

    public Link(Long projectId, String title, String url) {
        this.id = null;
        this.projectId = projectId;
        this.title = title;
        this.url = url;
    }

    
    public Link(Long id, Long projectId, String title, String url) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.url = url;
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

    public String getUrl() {
        return url;
    }
}