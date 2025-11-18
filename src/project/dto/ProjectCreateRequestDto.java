package src.project.dto;

import java.util.Map;
import java.util.Set;

public class ProjectCreateRequestDto {
    private final Long memberId;
    private final String title;
    private final String description;
    private final Set<String> techSpecs;
    private final Map<Long, String> mbtiMap;

    public ProjectCreateRequestDto(Long memberId, String title, String description, Set<String> techSpecs,  Map<Long, String> mbtiMap) {
        this.memberId = memberId;
        this.title = title;
        this.description = description;
        this.techSpecs = techSpecs;
        this.mbtiMap = mbtiMap;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getTechSpecs() {
        return techSpecs;
    }

    public Map<Long, String> getMbtiMap() {
        return mbtiMap;
    }
}
