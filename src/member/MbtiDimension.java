package src.member;

// DB의 MBTI 테이블 (마스터 데이터) 정보를
// Java 객체로 변환하기 위한 헬퍼 클래스(DTO)입니다.
public class MbtiDimension {
    Long id;
    String dimensionType; // "Energy", "Awareness", ...
    String option1; // "E", "S", ...
    String option2; // "I", "N", ...

    // MemberService에서 DB 결과를 객체로 만들 때 사용합니다.
    public MbtiDimension(Long id, String dimensionType, String option1, String option2) {
        this.id = id;
        this.dimensionType = dimensionType;
        this.option1 = option1;
        this.option2 = option2;
    }

    // Getter 메서드들 (Service에서 사용)
    public Long getId() { return id; }
    public String getDimensionType() { return dimensionType; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
}