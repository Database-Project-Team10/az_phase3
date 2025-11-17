package src.mbti.member;

import src.mbti.MbtiDimension;

import java.util.List;
import java.util.Map;

public class MemberMbtiService {

    private final MemberMbtiRepository mbtiRepository = new MemberMbtiRepository();

    public List<MbtiDimension> getMbtiDimensions() {
        return mbtiRepository.findAllMbtiDimensions();
    }

    public Map<Long, String> getMbtiMapByMemberId(Long memberId) {
        return mbtiRepository.findMbtiMapByMemberId(memberId);
    }

    public boolean saveMyMbti(Long memberId, Map<Long, String> mbtiMap) {
        return mbtiRepository.saveMemberMbti(memberId, mbtiMap);
    }
}