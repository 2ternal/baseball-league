package eternal.domain.teamMember;

import eternal.domain.custom.Position;
import eternal.domain.custom.TeamMemberShip;
import lombok.Data;

@Data
public class TeamMember {

    private Long teamMemberId;
    private Long memberId;
    private Long teamId;
    private TeamMemberShip memberShip;
    private Position mainPosition;

    public TeamMember() {
    }

    public TeamMember(Long teamMemberId, Long memberId, Long teamId, TeamMemberShip memberShip, Position mainPosition) {
        this.teamMemberId = teamMemberId;
        this.memberId = memberId;
        this.teamId = teamId;
        this.memberShip = memberShip;
        this.mainPosition = mainPosition;
    }
}
