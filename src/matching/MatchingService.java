package src.matching;

import java.util.List;

public class MatchingService {
    private final MatchingRepository matchingRepository = new MatchingRepository();

    public List<MatchedProject> getMbtiMatches(Long memberId) {
        return matchingRepository.findMbtiMatches(memberId);
    }

    public List<MatchedProject> getTechMatches(Long memberId) {
        return matchingRepository.findTechMatches(memberId);
    }

    public List<MatchedProject> getCombinedMatches(Long memberId) {
        return matchingRepository.findCombinedMatches(memberId);
    }
}