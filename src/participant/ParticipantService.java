package src.participant;

public class ParticipantService {
    private final ParticipantRepository participantRepository = new ParticipantRepository();

    public boolean joinProject(Long projectId, Long memberId){
        if (participantRepository.exists(projectId, memberId)){
            System.out.println("이미 참여 중인 프로젝트입니다.");
            return false;
        }
        return participantRepository.save(projectId, memberId);
    }
}
