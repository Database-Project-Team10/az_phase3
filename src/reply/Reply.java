package src.reply;

import java.time.LocalDateTime;

public class Reply {
    Long id;
    Long postId;
    Long memberId;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Reply(Long id, Long memberId, String content) {
        this.id = id;
        this.memberId = memberId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
