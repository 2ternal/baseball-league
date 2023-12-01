package eternal.baseball;

import eternal.baseball.domain.custom.Birthday;
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

        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("1234");
        member.setName("테스터");
        member.setBirthday(new Birthday(2000, 11, 5));

        memberRepository.save(member);

        Team team = new Team();
        team.setTeamName("테스트 1팀");
        team.setTeamCode("AAA");
        team.setOwner(member);

        teamRepository.save(team);

        TeamMember teamMember = new TeamMember();
        teamMember.addTeamMember(member, team, TeamMemberShip.OWNER);

        teamMemberRepository.save(teamMember);
    }
}
