package eternal.baseball.web.team;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CreateTeamForm {

    @NotEmpty
    private String teamName;
    @Size(min = 2, max = 5)
    @NotEmpty
    private String teamCode;
}
