package src.meeting;

import java.util.List;

public class MeetingService {

    private final MeetingRepository meetingRepository = new MeetingRepository();

    public boolean createMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByProject(Long projectId) {
        return meetingRepository.findByProjectId(projectId);
    }
    
    public Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    public boolean updateMeeting(Meeting meeting) {
        return meetingRepository.update(meeting);
    }

    public boolean deleteMeeting(Long meetingId) {
        return meetingRepository.delete(meetingId);
    }
}