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

        Member owner1 = Member.builder()
                .loginId("1234")
                .password("1234")
                .name("오너1")
                .birthday(new Birthday(1999, 11, 5))
                .build();

        Member owner2 = Member.builder()
                .loginId("123")
                .password("123")
                .name("오너2")
                .birthday(new Birthday(1998, 5, 15))
                .build();

        Member owner3 = Member.builder()
                .loginId("12345")
                .password("12345")
                .name("오너3")
                .birthday(new Birthday(1997, 5, 25))
                .build();

        memberRepository.save(owner1);
        memberRepository.save(owner2);
        memberRepository.save(owner3);

        Team team1 = Team.builder()
                .teamName("테스트 1팀")
                .teamCode("AAA")
                .owner(owner1)
                .build();
        Team team2 = Team.builder()
                .teamName("테스트 2팀")
                .teamCode("BBB")
                .owner(owner2)
                .build();
        Team team3 = Team.builder()
                .teamName("테스트 3팀")
                .teamCode("CCC")
                .owner(owner3)
                .build();

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        TeamMember teamMemberOwner1 = TeamMember.builder()
                .member(owner1)
                .team(team1)
                .memberShip(TeamMemberShip.OWNER)
                .mainPosition(Position.NONE)
                .backNumber(10L)
                .build();
        TeamMember teamMemberOwner2 = TeamMember.builder()
                .member(owner2)
                .team(team2)
                .memberShip(TeamMemberShip.OWNER)
                .mainPosition(Position.NONE)
                .backNumber(21L)
                .build();
        TeamMember teamMemberOwner3 = TeamMember.builder()
                .member(owner3)
                .team(team3)
                .memberShip(TeamMemberShip.OWNER)
                .mainPosition(Position.NONE)
                .backNumber(99L)
                .build();

        teamMemberRepository.save(teamMemberOwner1);
        teamMemberRepository.save(teamMemberOwner2);
        teamMemberRepository.save(teamMemberOwner3);

        for (int i = 1; i < 10; i++) {
            String memberLoginId = "test" + i;
            String memberPassword = "1234" + i;
            String memberName = "멤버" + i;
            Birthday birthday = new Birthday(2000 + i, 11, i);

            Member member1 = Member.builder()
                    .loginId(memberLoginId)
                    .password(memberPassword)
                    .name(memberName)
                    .birthday(birthday)
                    .build();
            memberRepository.save(member1);

            TeamMember teamMember1 = TeamMember.builder()
                    .member(member1)
                    .team(team1)
                    .memberShip(TeamMemberShip.PLAYER)
                    .mainPosition(Position.NONE)
                    .backNumber((long) i)
                    .build();
            teamMemberRepository.save(teamMember1);
        }

        for (int i = 10; i < 20; i++) {
            String memberLoginId = "test" + i;
            String memberPassword = "1234" + i;
            String memberName = "멤버" + i;
            Birthday birthday = new Birthday(2000 + i, 11, i);

            Member member2 = Member.builder()
                    .loginId(memberLoginId)
                    .password(memberPassword)
                    .name(memberName)
                    .birthday(birthday)
                    .build();
            memberRepository.save(member2);

            TeamMember teamMember2 = TeamMember.builder()
                    .member(member2)
                    .team(team2)
                    .memberShip(TeamMemberShip.PLAYER)
                    .mainPosition(Position.NONE)
                    .backNumber((long) i)
                    .build();
            teamMemberRepository.save(teamMember2);
        }

        for (int i = 21; i < 29; i++) {
            String memberLoginId = "test" + i;
            String memberPassword = "1234" + i;
            String memberName = "멤버" + i;
            Birthday birthday = new Birthday(2000 + i, 11, i);

            Member member3 = Member.builder()
                    .loginId(memberLoginId)
                    .password(memberPassword)
                    .name(memberName)
                    .birthday(birthday)
                    .build();
            memberRepository.save(member3);

            TeamMember teamMember3 = TeamMember.builder()
                    .member(member3)
                    .team(team3)
                    .memberShip(TeamMemberShip.PLAYER)
                    .mainPosition(Position.NONE)
                    .backNumber((long) i)
                    .build();
            teamMemberRepository.save(teamMember3);
        }
    }
}
