package eternal.baseball.domain.teamMember;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
import eternal.baseball.web.teamMember.JoinTeamMemberDto;
import eternal.baseball.web.teamMember.JoinTeamMemberForm;
import lombok.Data;

@Data
public class TeamMember {

    private Long teamMemberId;
    private Member member;
    private Team team;
    private TeamMemberShip memberShip;
    private Position mainPosition;
    private Long backNumber;

    public TeamMember() {
    }

    public TeamMember(Member member, Team team, JoinTeamMemberDto joinTeamMemberDto) {
        this.member = member;
        this.team = team;
        this.memberShip = TeamMemberShip.PLAYER;
        this.mainPosition = Position.from(joinTeamMemberDto.getMainPosition());
        this.backNumber = joinTeamMemberDto.getBackNumber();
    }

    public TeamMember(Member member, Team team, TeamMemberShip teamMemberShip) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
    }

    public TeamMember(Member member, Team team, TeamMemberShip teamMemberShip,
                      Position mainPosition, Long backNumber) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
        this.mainPosition = mainPosition;
        this.backNumber = backNumber;
    }

    public TeamMember(Member member, Team team, TeamMemberShip teamMemberShip,
                              JoinTeamMemberForm joinTeamMemberForm) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
        this.mainPosition = Position.valueOf(joinTeamMemberForm.getMainPosition());
        this.backNumber = joinTeamMemberForm.getBackNumber();
    }
}
