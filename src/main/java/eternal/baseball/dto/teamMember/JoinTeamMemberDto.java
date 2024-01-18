package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
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

    public JoinTeamMemberDto() {
    }

    public JoinTeamMemberDto(Member member, Team team) {
        this.teamName = team.getTeamName();
        this.memberName = member.getName();
    }
}
