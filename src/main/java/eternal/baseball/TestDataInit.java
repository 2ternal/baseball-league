package eternal.baseball;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.repository.MemberRepository;
import eternal.baseball.domain.Team;
import eternal.baseball.repository.TeamRepository;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.repository.TeamMemberRepository;
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

        Member owner3 = new Member();
        owner3.setLoginId("12345");
        owner3.setPassword("12345");
        owner3.setName("오너3");
        owner3.setBirthday(new Birthday(1997, 5, 25));

        memberRepository.save(owner1);
        memberRepository.save(owner2);
        memberRepository.save(owner3);

        Team team1 = new Team("테스트 1팀", "AAA", owner1);
        Team team2 = new Team("테스트 2팀", "BBB", owner2);
        Team team3 = new Team("테스트 3팀", "CCC", owner3);

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        TeamMember teamMemberOwner1 = new TeamMember(owner1, team1, TeamMemberShip.OWNER, Position.NONE, 10L);
        TeamMember teamMemberOwner2 = new TeamMember(owner2, team2, TeamMemberShip.OWNER, Position.NONE, 21L);
        TeamMember teamMemberOwner3 = new TeamMember(owner3, team3, TeamMemberShip.OWNER, Position.NONE, 99L);

        teamMemberRepository.save(teamMemberOwner1);
        teamMemberRepository.save(teamMemberOwner2);
        teamMemberRepository.save(teamMemberOwner3);

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

        for (int i = 21; i < 29; i++) {
            String memberLoginId = "test" + i;
            String memberPassword = "1234" + i;
            String memberName = "멤버" + i;
            Birthday birthday = new Birthday(2000 + i, 11, i);

            Member memberDTO = new Member();
            memberDTO.setLoginId(memberLoginId);
            memberDTO.setPassword(memberPassword);
            memberDTO.setName(memberName);
            memberDTO.setBirthday(birthday);

            memberRepository.save(memberDTO);

            TeamMember teamMember3 = new TeamMember(memberDTO, team3, TeamMemberShip.PLAYER, Position.NONE, (long) i);
            teamMemberRepository.save(teamMember3);
        }
    }
}
