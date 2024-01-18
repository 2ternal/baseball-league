package eternal.baseball.service;

import eternal.baseball.domain.Member;
import eternal.baseball.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    public final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    public Member joinMember(Member member) {
        return memberRepository.save(member);
    }

    /**
     * 멤버 정보 수정
     */
    public Member editMember(Long memberId, Member member) {
        return memberRepository.edit(memberId, member);
    }

    /**
     * 멤버 아이디로 member 찾기
     */
    public Member findByMemberId(Long MemberId) {
        return memberRepository.findByMemberId(MemberId);
    }

    /**
     * 모든 member 찾기
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * ID 유무 검증
     */
    public Boolean duplicateIdCheck(String memberId) {
        return !ObjectUtils.isEmpty(memberRepository.findByLoginId(memberId));
    }

    /**
     * 이름 유무 검증
     */
    public Boolean duplicateNameCheck(String name) {
        return !ObjectUtils.isEmpty(memberRepository.findByName(name));
    }
}
