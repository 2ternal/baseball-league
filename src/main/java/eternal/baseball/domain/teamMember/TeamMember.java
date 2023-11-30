package eternal.baseball.domain.teamMember;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
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

    public void addTeamMember(Member member, Team team, TeamMemberShip teamMemberShip) {
        this.memberId = member.getMemberId();
        this.teamId = team.getTeamId();
        this.memberShip = teamMemberShip;
        this.mainPosition = Position.NONE;
    }

    public void addTeamMember(Member member, Team team, TeamMemberShip teamMemberShip, Position position) {
        this.memberId = member.getMemberId();
        this.teamId = team.getTeamId();
        this.memberShip = teamMemberShip;
        this.mainPosition = position;
    }
}
