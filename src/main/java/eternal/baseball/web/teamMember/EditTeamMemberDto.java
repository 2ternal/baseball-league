package eternal.baseball.web.teamMember;

import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EditTeamMemberDto {

    private String teamName;
    private String memberName;
    @NotEmpty
    private String mainPosition;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;
    @NotEmpty
    private String teamMemberShip;

    public EditTeamMemberDto() {
    }

    public EditTeamMemberDto(TeamMember teamMember) {
        this.teamName = teamMember.getTeam().getTeamName();
        this.memberName = teamMember.getMember().getName();
        this.mainPosition = teamMember.getMainPosition().getDescription();
        this.backNumber = teamMember.getBackNumber();
        this.teamMemberShip = teamMember.getMemberShip().getDescription();
    }
}
