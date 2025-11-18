package src.project;

import java.time.LocalDateTime;

public class Project {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public Project(Long id, String title, String description, LocalDateTime createdAt,  LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Project(Long id, String title, String description, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.modifiedAt = LocalDateTime.now();
    }

    public Project(String title, String description) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

}
