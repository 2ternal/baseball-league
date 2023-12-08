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

        Member owner1 = new Member();
        owner1.setLoginId("1234");
        owner1.setPassword("1234");
        owner1.setName("오너1");
        owner1.setBirthday(new Birthday(1999, 11, 5));

        Member owner2 = new Member();
        owner2.setLoginId("123");
        owner2.setPassword("123");
        owner2.setName("오너2");
        owner2.setBirthday(new Birthday(1998, 5, 15));

        memberRepository.save(owner1);
        memberRepository.save(owner2);

        Team team1 = new Team("테스트 1팀", "AAA", owner1);
        Team team2 = new Team("테스트 2팀", "BBB", owner2);

        teamRepository.save(team1);
        teamRepository.save(team2);

        TeamMember teamMemberOwner1 = new TeamMember(owner1, team1, TeamMemberShip.OWNER, Position.NONE, 10L);
        TeamMember teamMemberOwner2 = new TeamMember(owner2, team2, TeamMemberShip.OWNER, Position.NONE, 21L);

        teamMemberRepository.save(teamMemberOwner1);
        teamMemberRepository.save(teamMemberOwner2);

        for (int i = 1; i < 10; i++) {
            String memberLoginId = "test" + i;
            String memberPassword = "1234" + i;
            String memberName = "멤버" + i;
            Birthday birthday = new Birthday(2000 + i, 11, i);

            Member member = new Member();
            member.setLoginId(memberLoginId);
            member.setPassword(memberPassword);
            member.setName(memberName);
            member.setBirthday(birthday);

            memberRepository.save(member);

            TeamMember teamMember1 = new TeamMember(member, team1, TeamMemberShip.PLAYER, Position.NONE, (long) i);
            teamMemberRepository.save(teamMember1);
        }

        for (int i = 10; i < 20; i++) {
            String memberLoginId = "test" + i;
            String memberPassword = "1234" + i;
            String memberName = "멤버" + i;
            Birthday birthday = new Birthday(2000 + i, 11, i);

            Member member = new Member();
            member.setLoginId(memberLoginId);
            member.setPassword(memberPassword);
            member.setName(memberName);
            member.setBirthday(birthday);

            memberRepository.save(member);

            TeamMember teamMember2 = new TeamMember(member, team2, TeamMemberShip.PLAYER, Position.NONE, (long) i);
            teamMemberRepository.save(teamMember2);
        }
    }
}
