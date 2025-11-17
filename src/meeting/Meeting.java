package src.meeting;

import java.time.LocalDateTime;

public class Meeting {
    Long id;          // PK (meeting_id)
    Long projectId;   // FK (project_id)
    String title;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime; 

    public Meeting(Long projectId, String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Meeting(Long id, Long projectId, String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}