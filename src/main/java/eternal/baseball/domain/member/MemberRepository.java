package eternal.baseball.domain.member;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepository {

    private static final Map<Long, Member> memberStore = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * 새 멤버 추가
     */
    public Member save(Member member) {
        member.setMemberId(++sequence);
        memberStore.put(member.getMemberId(), member);
        return member;
    }

    /**
     * 멤버 정보 수정
     */
    public Member edit(Long memberId, Member member) {
        memberStore.put(memberId, member);
        return member;
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
