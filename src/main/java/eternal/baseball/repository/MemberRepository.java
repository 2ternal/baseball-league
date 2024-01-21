package eternal.baseball.repository;

import eternal.baseball.domain.Member;
import eternal.baseball.dto.member.MemberDTO;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepository {

    private static final Map<Long, Member> memberStore = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * 새 멤버 추가
     */
    public MemberDTO save(Member member) {
        member.setMemberId(++sequence);
        memberStore.put(member.getMemberId(), member);
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthday(member.getBirthday())
                .build();
    }

    /**
     * 멤버 정보 수정
     */
    public MemberDTO edit(Long memberId, Member member) {
        memberStore.put(memberId, member);
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthday(member.getBirthday())
                .build();
    }

    /**
     * 멤버 아이디로 member 찾기
     */
    public Member findByMemberId(Long MemberId) {
        return memberStore.get(MemberId);
    }

    /**
     * 로그인 아이디로 member 찾기
     */
    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    /**
     * 로그인 아이디로 member 찾기
     */
    public Optional<Member> findByName(String name) {
        return findAll().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst();
    }

    /**
     * 모든 member 찾기
     */
    public List<Member> findAll() {
        return new ArrayList<>(memberStore.values());
    }

    public void clearStore() {
        memberStore.clear();
    }
}
