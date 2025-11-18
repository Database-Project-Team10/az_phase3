package src.participant.exception;

public class ParticipantAlreadyExistsException extends ParticipantException {
    public ParticipantAlreadyExistsException() {
        super("이미 참여 중인 프로젝트입니다.");
    }
}
