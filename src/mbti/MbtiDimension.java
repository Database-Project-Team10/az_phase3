package src.mbti;

public class MbtiDimension {
    Long id;
    String dimensionType; // "Energy", "Awareness", ...
    String option1; // "E", "S", ...
    String option2; // "I", "N", ...

    public MbtiDimension(Long id, String dimensionType, String option1, String option2) {
        this.id = id;
        this.dimensionType = dimensionType;
        this.option1 = option1;
        this.option2 = option2;
    }

    // Controller에서 질문을 만들 때 사용
    public Long getId() { return id; }
    public String getDimensionType() { return dimensionType; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
}