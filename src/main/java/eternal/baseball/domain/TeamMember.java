package eternal.baseball.domain;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.teamMember.EditTeamMemberDto;
import eternal.baseball.dto.teamMember.JoinTeamMemberDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    private Long teamMemberId;
    private Member member;
    private Team team;
    private TeamMemberShip memberShip;
    private Position mainPosition;
    private Long backNumber;

    public TeamMember(Member member, Team team, JoinTeamMemberDto joinTeamMemberDto) {
        this.member = member;
        this.team = team;
        this.memberShip = TeamMemberShip.PLAYER;
        this.mainPosition = Position.fromEng(joinTeamMemberDto.getMainPosition());
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

    public TeamMember(TeamMember teamMember, EditTeamMemberDto editTeamMemberDto) {
        this.teamMemberId = teamMember.getTeamMemberId();
        this.member = teamMember.getMember();
        this.team = teamMember.getTeam();
        this.memberShip = TeamMemberShip.fromDescription(editTeamMemberDto.getTeamMemberShip());
        this.mainPosition = Position.fromDescription(editTeamMemberDto.getMainPosition());
        this.backNumber = editTeamMemberDto.getBackNumber();
    }
}
