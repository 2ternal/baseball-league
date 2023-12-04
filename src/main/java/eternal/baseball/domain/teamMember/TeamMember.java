package eternal.baseball.domain.teamMember;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
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

    public TeamMember(Long teamMemberId, Member member, Team team,
                      TeamMemberShip memberShip, Position mainPosition, Long backNumber) {
        this.teamMemberId = teamMemberId;
        this.member = member;
        this.team = team;
        this.memberShip = memberShip;
        this.mainPosition = mainPosition;
        this.backNumber = backNumber;
    }

    public void addTeamMember(Member member, Team team, TeamMemberShip teamMemberShip) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
    }

    public void addTeamMember(Member member, Team team, TeamMemberShip teamMemberShip,
                              Position position) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
        this.mainPosition = position;
    }

    public void addTeamMember(Member member, Team team, TeamMemberShip teamMemberShip,
                              JoinTeamMemberForm joinTeamMemberForm) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
        this.mainPosition = Position.valueOf(joinTeamMemberForm.getMainPosition());
        this.backNumber = joinTeamMemberForm.getBackNumber();
    }
}
