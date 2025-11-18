package src.techspec.member;

public class MemberTechspec {
    Long memberId;
    Long techspecId;

    public MemberTechspec(Long memberId, Long techspecId) {
        this.memberId = memberId;
        this.techspecId = techspecId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getTechspecId() {
        return techspecId;
    }
}
