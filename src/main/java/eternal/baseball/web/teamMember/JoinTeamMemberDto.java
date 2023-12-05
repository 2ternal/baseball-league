package eternal.baseball.web.teamMember;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class JoinTeamMemberDto {

    private String teamName;
    private String memberName;
    @NotEmpty
    private String mainPosition;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;

    public Position[] positions = Position.values();

    public JoinTeamMemberDto() {
    }

    public JoinTeamMemberDto(Member member, Team team) {
        this.teamName = team.getTeamName();
        this.memberName = member.getName();
    }

    public TeamMember toTeamMember(Member member, Team team) {
        return new TeamMember(member, team, this);
    }
}
