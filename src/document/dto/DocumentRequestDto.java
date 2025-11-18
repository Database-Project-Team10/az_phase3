package src.document.dto;

public class DocumentRequestDto {
    String title;
    String location;

    public DocumentRequestDto(String title, String location) {
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }
}