package src.reply.dto;

public class ReplyResponseDto {
    Long id;
    String content;
    String name;

    public ReplyResponseDto(Long id, String content, String name) {
        this.id = id;
        this.content = content;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
