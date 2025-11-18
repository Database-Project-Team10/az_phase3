package src.post;

public class PostCreateRequestDto {
    String title;
    String content;

    public PostCreateRequestDto(String title, String content) {
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
