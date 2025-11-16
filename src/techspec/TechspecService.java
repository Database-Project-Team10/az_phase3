package src.techspec;

import src.member.Member;
import java.util.List;

public class TechspecService {
    private final TechspecRepository techspecRepository = new TechspecRepository();

    /**
     * 1. 내 스택 목록 보기
     * @param currentUser 현재 로그인한 사용자
     */
    public void viewMyTechspecs(Member currentUser) {
        System.out.println("\n---------- " + currentUser.getName() + "님의 스택 목록 ----------");

        List<String> myTechs = techspecRepository.findTechspecsByMemberId(currentUser.getId());

        if (myTechs.isEmpty()) {
            System.out.println("아직 등록된 스펙이 없습니다.");
        }else{
            for (String techName : myTechs) {
                System.out.println("- "+techName);
            }
        }
    }

    /**
     * 2. 스택 추가
     * @param currentUser 현재 로그인한 사용자
     * @param techName [!] Controller에서 입력받은 기술 이름
     */
    public void addTechspec(Member currentUser,String techName) {
        System.out.println("\n---------- (개발 중) 스택 추가 ----------");
        System.out.println(currentUser.getEmail() + "님에게 '" + techName + "' 스택을 추가합니다.");

        // (다음 로직)
        // 2. techspecRepository.findTechspecByName("Java") 호출 -> techspec_id 찾기
        // 3. techspecRepository.addMemberTechspec(currentUser.getId(), techspec_id) 호출
    }

    /**
     * (개발 중) 3. 스택 삭제 (D)
     * @param currentUser 현재 로그인한 사용자
     * @param techName [!] Controller에서 입력받은 기술 이름
     */
    public void removeTechspec(Member currentUser, String techName) {
        System.out.println("\n---------- (개발 중) 스택 삭제 ----------");
        System.out.println(currentUser.getEmail() + "님에게서 '" + techName + "' 스택을 삭제합니다.");

        // (다음 로직)
        // 2. techspecRepository.findTechspecByName("Java") 호출 -> techspec_id 찾기
        // 3. techspecRepository.deleteMemberTechspec(currentUser.getId(), techspec_id) 호출
    }
}
