package src.link;


public class Link {
    Long id;          // PK (link_id)
    Long projectId;   // FK (project_id)
    String title;
    String url;

    // (C) Create: Service가 Controller로부터 입력을 받아 Repository로 전달할 때 사용하는 생성자 (id가 없음)
    public Link(Long projectId, String title, String url) {
        this.projectId = projectId;
        this.title = title;
        this.url = url;
    }

    
     // (R) Read: Repository가 DB 조회 결과를 Service로 반환할 때 사용하는 생성자 (모든 필드 포함)
    public Link(Long id, Long projectId, String title, String url) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.url = url;
    }

    // --- Getter 메서드 ---

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