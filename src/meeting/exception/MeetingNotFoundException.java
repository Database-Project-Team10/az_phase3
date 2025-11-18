package src.meeting.exception;

public class MeetingNotFoundException extends MeetingException {
    public MeetingNotFoundException() {
        super("해당 회의록을 찾을 수 없습니다.");
    }
}