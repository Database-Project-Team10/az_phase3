package src.mbti; // [!] 새 패키지

import java.util.List;
import java.util.Map;

/**
 * [Member]의 MBTI 관련 비즈니스 로직을 담당
 */
public class MemberMbtiService {

    private final MemberMbtiRepository mbtiRepository = new MemberMbtiRepository();

    /**
     * (R) Controller가 "질문지"를 요청
     */
    public List<MbtiDimension> getMbtiDimensions() {
        return mbtiRepository.findAllMbtiDimensions();
    }

    /**
     * (R) Controller가 "기존 답안지"를 요청
     */
    public Map<Long, String> getMbtiMapByMemberId(Long memberId) {
        return mbtiRepository.findMbtiMapByMemberId(memberId);
    }

    /**
     * (C/U) Controller가 "새 답안지" 저장을 요청
     */
    public boolean saveMyMbti(Long memberId, Map<Long, String> mbtiMap) {
        return mbtiRepository.saveMemberMbti(memberId, mbtiMap);
    }
}