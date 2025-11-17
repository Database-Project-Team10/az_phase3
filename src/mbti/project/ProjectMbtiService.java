package src.mbti.project;

import src.mbti.MbtiDimension;
import src.mbti.exception.InvalidMbtiException;
import src.mbti.exception.MbtiNotFoundException;
import src.mbti.member.MemberMbtiRepository;

import java.util.List;
import java.util.Map;

public class ProjectMbtiService {

    private final ProjectMbtiRepository projectMbtiRepository = new ProjectMbtiRepository();
    private final MemberMbtiRepository mbtiRepository = new MemberMbtiRepository();

    public List<MbtiDimension> getMbtiDimensions() {
        List<MbtiDimension> dimensions = mbtiRepository.findAllMbtiDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            throw new MbtiNotFoundException("MBTI 차원 정보를 불러올 수 없습니다.");
        }
        return dimensions;
    }

    public Map<Long, String> getMbtiMapByProjectId(Long projectId) {
        if (projectId == null) {
            throw new InvalidMbtiException("프로젝트 ID가 필요합니다.");
        }

        Map<Long, String> map = projectMbtiRepository.findMbtiMapByProjectId(projectId);

        if (map == null) {
            throw new MbtiNotFoundException("해당 프로젝트의 MBTI 정보를 찾을 수 없습니다.");
        }

        return map;
    }

    public void saveProjectMbti(Long projectId, Map<Long, String> mbtiMap) {
        if (projectId == null) {
            throw new InvalidMbtiException("프로젝트 ID가 필요합니다.");
        }

        if (mbtiMap == null || mbtiMap.isEmpty()) {
            throw new InvalidMbtiException("MBTI 정보가 비어있습니다.");
        }

        projectMbtiRepository.saveProjectMbti(projectId, mbtiMap);
    }
}