package src.member.dto;

import java.time.LocalDate;

public class MemberInfoResponseDto {
    Long id;
    String name;
    String email;
    LocalDate birthDate;
    String mbti;
    String techspecs;
    String projectAndRole;

    public MemberInfoResponseDto(Long id, String name, String email, LocalDate birthDate, String mbti, String techspecs, String projectAndRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.mbti = mbti;
        this.techspecs = techspecs;
        this.projectAndRole = projectAndRole;
    }


    @Override
    public String toString() {
        // 1. 테크스펙 포맷팅
        String formattedTechs;
        if (techspecs == null || techspecs.isEmpty()) {
            formattedTechs = " 없음";
        } else {
            formattedTechs = "\n    - " + techspecs.replace(", ", "\n    - ");
        }

        // 2. 프로젝트 및 역할 포맷팅
        String formattedProjects;
        if (projectAndRole == null || projectAndRole.isEmpty()) {
            formattedProjects = " 없음";
        } else {
            formattedProjects = "\n    - " + projectAndRole.replace("; ", "\n    - ");
        }

        // 3. 최종 문자열 조합
        return String.format(
                "---------- 사용자 정보 ----------\n" +
                        "- 이름: '%s'\n" +
                        "- 이메일: '%s'\n" +
                        "- 생일: %s\n" +
                        "- MBTI: '%s'\n" +
                        "- 테크스펙:%s\n" +
                        "- 참가 프로젝트 및 역할:%s",
                name, email, birthDate, mbti, formattedTechs, formattedProjects
        );
    }
}
