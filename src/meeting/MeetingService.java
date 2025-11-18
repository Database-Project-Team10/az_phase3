package src.meeting;

import java.util.List;
import java.time.LocalDateTime;
import src.meeting.exception.MeetingAccessException;
import src.meeting.exception.InvalidMeetingInputException;

public class MeetingService {

    private final MeetingRepository meetingRepository;
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    private void validateMeetingTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null && endTime == null) {
            return; 
        }
        
        if (startTime == null || endTime == null) {
            throw new InvalidMeetingInputException("시작 시간과 종료 시간은 함께 비워두거나, 함께 입력해야 합니다.");
        }
        
        if (!endTime.isAfter(startTime)) {
            throw new InvalidMeetingInputException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }
    }
    public boolean createMeeting(Meeting meeting) {
        validateMeetingTime(meeting.getStartTime(), meeting.getEndTime()); // [!] 2. 저장 전 검증
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByProject(Long projectId) {
        return meetingRepository.findByProjectId(projectId);
    }
    
    public Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    public boolean updateMeeting(Meeting updatedMeeting, Long expectedProjectId) {
        Meeting targetMeeting = meetingRepository.findById(updatedMeeting.getId());

        if (targetMeeting == null || !targetMeeting.getProjectId().equals(expectedProjectId)) {
            throw new MeetingAccessException("수정 권한이 없거나 유효하지 않은 회의록 ID입니다.");
        }

        validateMeetingTime(updatedMeeting.getStartTime(), updatedMeeting.getEndTime());
        
        return meetingRepository.update(updatedMeeting);
    }

    public boolean deleteMeeting(Long meetingId, Long expectedProjectId) {
        Meeting targetMeeting = meetingRepository.findById(meetingId);

        if (targetMeeting == null || !targetMeeting.getProjectId().equals(expectedProjectId)) {
            throw new MeetingAccessException("삭제 권한이 없거나 유효하지 않은 회의록 ID입니다.");
        }
        
        return meetingRepository.delete(meetingId);
    }
}