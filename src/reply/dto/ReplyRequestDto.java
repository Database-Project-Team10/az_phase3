package src.reply.dto;

public class ReplyRequestDto {
    private final String content;

    public ReplyRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
