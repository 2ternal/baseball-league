package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.member.MemberDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class RequestTeamMemberDTO {

    private String teamCode;
    private MemberDTO member;
    private TeamMemberShip teamMemberShip;
    private Long backNumber;
    private String mainPositionEng;

    public TeamMember toEntity(Member member, Team team) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .memberShip(teamMemberShip)
                .mainPosition(Position.fromEng(mainPositionEng))
                .backNumber(backNumber)
                .build();
    }
}
