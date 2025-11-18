package src.meeting;

import java.util.List;
import java.time.LocalDateTime;
import src.meeting.dto.MeetingRequestDto;
import src.meeting.exception.MeetingAccessException;
import src.meeting.exception.InvalidMeetingInputException;
import src.meeting.exception.MeetingNotFoundException;

public class MeetingService {

    private final MeetingRepository meetingRepository;
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    private void validateMeetingTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null && endTime == null) return;
        
        if (startTime == null || endTime == null) {
            throw new InvalidMeetingInputException("시작 시간과 종료 시간은 함께 비워두거나, 함께 입력해야 합니다.");
        }
        
        if (!endTime.isAfter(startTime)) {
            throw new InvalidMeetingInputException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }
    }
    public void createMeeting(Long projectId, MeetingRequestDto requestDto) {
        if (requestDto.getTitle().trim().isEmpty()) {
            throw new InvalidMeetingInputException("회의록 제목은 비워둘 수 없습니다.");
        }
        
        validateMeetingTime(requestDto.getStartTime(), requestDto.getEndTime());
        
        Meeting meeting = new Meeting(
            projectId, 
            requestDto.getTitle(), 
            requestDto.getDescription(), 
            requestDto.getStartTime(), 
            requestDto.getEndTime()
        );
        
        meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByProject(Long projectId) {
        return meetingRepository.findByProjectId(projectId);
    }
    
    public Meeting getMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId);
        if (meeting == null) {
            throw new MeetingNotFoundException();
        }
        return meeting;
    }

    public void updateMeeting(Long meetingId, Long projectId, MeetingRequestDto requestDto) {
        if (requestDto.getTitle().trim().isEmpty()) {
            throw new InvalidMeetingInputException("회의록 제목은 비워둘 수 없습니다.");
        }
        
        Meeting targetMeeting = meetingRepository.findById(meetingId);
        if (targetMeeting == null) {
            throw new MeetingNotFoundException();
        }

        if (!targetMeeting.getProjectId().equals(projectId)) {
            throw new MeetingAccessException("해당 회의록은 이 프로젝트에 속하지 않아 수정할 수 없습니다.");
        }

        validateMeetingTime(requestDto.getStartTime(), requestDto.getEndTime());
        
        Meeting meeting = new Meeting(
            meetingId,
            projectId,
            requestDto.getTitle(),
            requestDto.getDescription(),
            requestDto.getStartTime(),
            requestDto.getEndTime()
        );

        meetingRepository.update(meeting);
    }

    public void deleteMeeting(Long meetingId, Long expectedProjectId) {
        Meeting targetMeeting = meetingRepository.findById(meetingId);
        if (targetMeeting == null) {
            throw new MeetingNotFoundException();
        }

        if (!targetMeeting.getProjectId().equals(expectedProjectId)) {
            throw new MeetingAccessException("해당 회의록은 이 프로젝트에 속하지 않아 삭제할 수 없습니다.");
        }
        
        meetingRepository.delete(meetingId);
    }
}