package src.participant;

import src.participant.exception.ParticipantAlreadyExistsException;
import src.project.ProjectService;
import src.project.exception.ProjectNotFoundException;

public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ProjectService projectService;

    public ParticipantService(ParticipantRepository participantRepository,
                              ProjectService projectService) {
        this.participantRepository = participantRepository;
        this.projectService = projectService;
    }


    public void joinProject(Long projectId, Long memberId){

        if (projectService.getProject(projectId) == null) {
            throw new ProjectNotFoundException();
        }

        if (participantRepository.exists(projectId, memberId)){
            throw new ParticipantAlreadyExistsException();
        }
        participantRepository.save(projectId, memberId);
    }
}
