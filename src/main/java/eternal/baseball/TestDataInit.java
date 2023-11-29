package eternal.baseball;

import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final MemberRepository memberRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {

        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("1234");
        member.setName("테스터");
        member.setBirthday(new Birthday(2000, 11, 5));

        memberRepository.save(member);
    }
}
