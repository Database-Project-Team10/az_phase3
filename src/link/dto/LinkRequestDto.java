package src.link.dto;

public class LinkRequestDto {
    String title;
    String url;

    public LinkRequestDto(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }


}
