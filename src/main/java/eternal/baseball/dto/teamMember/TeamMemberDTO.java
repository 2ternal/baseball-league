package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.TeamDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TeamMemberDTO {

    private Long teamMemberId;
    private MemberDTO member;
    private TeamDTO team;
    private TeamMemberShip memberShip;
    private Position mainPosition;
    private Long backNumber;
}
