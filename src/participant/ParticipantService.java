package src.participant;

import src.participant.exception.ParticipantAlreadyExistsException;

public class ParticipantService {
    private final ParticipantRepository participantRepository = new ParticipantRepository();

    public void joinProject(Long projectId, Long memberId){
        if (participantRepository.exists(projectId, memberId)){
            throw new ParticipantAlreadyExistsException();
        }
        participantRepository.save(projectId, memberId);
    }
}
