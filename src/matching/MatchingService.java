package src.matching;

import java.util.List;

public class MatchingService {

    private final MatchingRepository matchingRepository;
    public MatchingService(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

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