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

    public Post (Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
