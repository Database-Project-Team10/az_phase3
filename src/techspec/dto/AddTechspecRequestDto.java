package src.techspec.dto;

public class AddTechspecRequestDto {
    private final String name;

    public String getName() {
        return name;
    }

    public AddTechspecRequestDto(String name) {
        this.name = name;
    }
}
