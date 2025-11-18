package src.project.dto;

public class ProjectUpdateRequestDto {
    private final String title;
    private final String description;

    public ProjectUpdateRequestDto(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
