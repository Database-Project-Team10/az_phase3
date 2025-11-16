package src.reply;

import java.time.LocalDateTime;

public class Reply {
    Long id;
    Long postId;
    Long memberId;
    String content;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public Reply(Long postId, Long memberId, String content) {
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Reply(Long id, Long memberId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
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

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
}
