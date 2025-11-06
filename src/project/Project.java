package src.project;

import java.time.LocalDateTime;

public class Project {
    Long id;
    String title;
    String description;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public Project(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
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
