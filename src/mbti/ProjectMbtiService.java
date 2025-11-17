package src.mbti;

import java.util.List;
import java.util.Map;

public class ProjectMbtiService {

    private final ProjectMbtiRepository projectMbtiRepository = new ProjectMbtiRepository();
    private final MemberMbtiRepository mbtiRepository = new MemberMbtiRepository();

    public List<MbtiDimension> getMbtiDimensions() {
        return mbtiRepository.findAllMbtiDimensions();
    }


    public Map<Long, String> getMbtiMapByProjectId(Long projectId) {
        return projectMbtiRepository.findMbtiMapByProjectId(projectId);
    }

    public boolean saveProjectMbti(Long projectId, Map<Long, String> mbtiMap) {
        return projectMbtiRepository.saveProjectMbti(projectId, mbtiMap);
    }
}