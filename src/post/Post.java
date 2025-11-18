package src.post;

import java.time.LocalDateTime;

public class Post {
    Long id;
    Long projectId;
    Long memberId;

    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public Post(Long id, Long projectId, Long memberId, String title, String content, LocalDateTime createdAt,  LocalDateTime modifiedAt) {
        this.id = id;
        this.projectId = projectId;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Post(Long id, Long memberId, String title, String content, LocalDateTime createdAt){
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = LocalDateTime.now();
    }

    public Post(Long projectId, Long memberId, String title, String content) {
        this.projectId = projectId;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


}
