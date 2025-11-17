package src.matching;

public class MatchedProject {
    private Long id;
    private String title;
    private int score; // 매칭 개수 또는 종합 점수
    private String description; // 매칭된 내역 (MBTI 옵션들 또는 기술 이름들)

    public MatchedProject(Long id, String title, int score, String description) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.description = description;
    }

    // Getter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public int getScore() { return score; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("[%d] %s (점수: %d) - %s", id, title, score, description);
    }
}