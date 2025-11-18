package src.participant;

import src.participant.exception.ParticipantAlreadyExistsException;

public class ParticipantService {
    private final ParticipantRepository participantRepository = new ParticipantRepository();

    public boolean joinProject(Long projectId, Long memberId){
        if (participantRepository.exists(projectId, memberId)){
            throw new ParticipantAlreadyExistsException();
        }
        return participantRepository.save(projectId, memberId);
    }
}
