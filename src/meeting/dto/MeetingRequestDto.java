package src.meeting.dto;

import java.time.LocalDateTime;

public class MeetingRequestDto {
    String title;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;

    public MeetingRequestDto(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
