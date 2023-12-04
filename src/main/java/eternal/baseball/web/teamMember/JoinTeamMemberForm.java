package eternal.baseball.web.teamMember;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class JoinTeamMemberForm {

    @NotEmpty
    private String mainPosition;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;

    public JoinTeamMemberForm() {
    }

    public JoinTeamMemberForm(String mainPosition, Long backNumber) {
        this.mainPosition = mainPosition;
        this.backNumber = backNumber;
    }
}
