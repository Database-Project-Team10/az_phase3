package src.techspec;

import src.member.Member;
import src.techspec.TechspecRepository;

public class TechspecService {
    private final TechspecRepository techspecRepository = new TechspecRepository();

    /**
     * (개발 중) 1. 내 스택 목록 보기 (R)
     * @param currentUser 현재 로그인한 사용자
     */
    public void viewMyTechspecs(Member currentUser) {
        System.out.println("\n---------- (개발 중) 내 스택 목록 보기 ----------");
        System.out.println(currentUser.getEmail() + "님의 스택을 조회합니다.");

        // (다음 로직)
        // 1. techspecRepository.findTechspecsByMemberId(currentUser.getId()) 호출
        // 2. 반환된 List<String> (기술 이름 리스트)를 출력
    }

    /**
     * (개발 중) 2. 스택 추가 (C)
     * @param currentUser 현재 로그인한 사용자
     */
    public void addTechspec(Member currentUser) {
        System.out.println("\n---------- (개발 중) 스택 추가 ----------");

        // (다음 로직)
        // 1. (Controller에서 입력받은 기술 이름(예: "Java")을 파라미터로 받기)
        // 2. techspecRepository.findTechspecByName("Java") 호출 -> techspec_id 찾기
        // 3. techspecRepository.addMemberTechspec(currentUser.getId(), techspec_id) 호출
    }

    /**
     * (개발 중) 3. 스택 삭제 (D)
     * @param currentUser 현재 로그인한 사용자
     */
    public void removeTechspec(Member currentUser) {
        System.out.println("\n---------- (개발 중) 스택 삭제 ----------");

        // (다음 로직)
        // 1. (Controller에서 입력받은 기술 이름(예: "Java")을 파라미터로 받기)
        // 2. techspecRepository.findTechspecByName("Java") 호출 -> techspec_id 찾기
        // 3. techspecRepository.deleteMemberTechspec(currentUser.getId(), techspec_id) 호출
    }
}
