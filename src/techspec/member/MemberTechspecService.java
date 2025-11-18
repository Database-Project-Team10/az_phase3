package src.techspec.member;

import src.member.Member;
import src.techspec.Techspec;
import src.techspec.TechspecRepository;
import src.techspec.dto.TechspecAddRequestDto;
import src.techspec.exception.TechspecAlreadyExistsException;
import src.techspec.exception.TechspecInvalidException;
import src.techspec.exception.TechspecNotFoundException;
import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MemberTechspecService {

    private final TechspecRepository techspecRepository;
    private final MemberTechspecRepository memberTechspecRepository;
    public MemberTechspecService(TechspecRepository techspecRepository,  MemberTechspecRepository memberTechspecRepository) {
        this.techspecRepository = techspecRepository;
        this.memberTechspecRepository = memberTechspecRepository;
    }

    public List<Techspec> getMyTechspecs(Member currentUser) {
        return memberTechspecRepository.findTechspecsByMemberId(currentUser.getId());
    }

    public void addTechspec(Member currentUser, TechspecAddRequestDto techspecAddRequestDto) {

        if (techspecAddRequestDto.getName() == null || techspecAddRequestDto.getName().isBlank()) {
            throw new TechspecInvalidException("스택 이름은 비어 있을 수 없습니다.");
        }

        Connection conn = null;

        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false);

            Techspec techspec = techspecRepository.findTechspecIdByName(techspecAddRequestDto.getName());
            Long techspecId = techspec.getId();
            // 스택이 없으면 생성
            if (techspec == null) {
                techspecId = techspecRepository.createTechspec(conn, techspecAddRequestDto.getName());
            }

            if (memberTechspecRepository.addMemberTechspec(conn, currentUser.getId(), techspecId) == null) {
                throw new RuntimeException("스택 추가 실패");
            }

            conn.commit();

        } catch (SQLException e) {

            rollbackQuietly(conn);

            if (e.getErrorCode() == 1) {
                throw new TechspecAlreadyExistsException(
                        "이미 존재하는 스택입니다."
                );
            }

            throw new RuntimeException("DB 오류: " + e.getMessage());

        } finally {
            closeQuietly(conn);
        }
    }

    public void removeTechspec(Member currentUser, Long techspecId) {

        if (techspecId == null || techspecId <= 0) {
            throw new TechspecInvalidException("유효하지 않은 스택 ID입니다.");
        }

        if (!memberTechspecRepository.deleteMemberTechspec(currentUser.getId(), techspecId)) {
            throw new TechspecNotFoundException("삭제할 스택이 존재하지 않습니다.");
        }
    }


    private void rollbackQuietly(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ignored) {}
    }

    private void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ignored) {}
    }
}