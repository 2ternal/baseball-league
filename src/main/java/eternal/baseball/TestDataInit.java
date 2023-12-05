package eternal.baseball;

import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.member.MemberRepository;
import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.team.TeamRepository;
import eternal.baseball.domain.teamMember.TeamMember;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {

        Member member1 = new Member();
        member1.setLoginId("test");
        member1.setPassword("1234");
        member1.setName("테스터");
        member1.setBirthday(new Birthday(2000, 11, 5));

        Member member2 = new Member();
        member2.setLoginId("1234");
        member2.setPassword("1234");
        member2.setName("테스터2");
        member2.setBirthday(new Birthday(1999, 5, 15));

        memberRepository.save(member1);
        memberRepository.save(member2);

        Team team = new Team();
        team.setTeamName("테스트 1팀");
        team.setTeamCode("AAA");
        team.setOwner(member1);

        teamRepository.save(team);

        TeamMember teamMember = new TeamMember(member1, team, TeamMemberShip.OWNER, Position.NONE, 10L);

        teamMemberRepository.save(teamMember);
    }
}
