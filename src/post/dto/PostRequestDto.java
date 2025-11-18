package src.post.dto;

public class PostRequestDto {
    String title;
    String content;

    public PostRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
