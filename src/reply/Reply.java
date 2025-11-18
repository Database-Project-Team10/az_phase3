package src.reply;

import java.time.LocalDateTime;

public class Reply {
    private final Long id;
    private final Long postId;
    private final Long memberId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public Reply(Long id, Long postId, Long memberId, String content, LocalDateTime createdAt, LocalDateTime modifiedAt){
        this.id = id;
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Reply(Long id, Long postId, Long memberId, String content, LocalDateTime createdAt){
        this.id = id;
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = LocalDateTime.now();
    }

    public Reply(Long postId, Long memberId, String content) {
        this.id = null;
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
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
