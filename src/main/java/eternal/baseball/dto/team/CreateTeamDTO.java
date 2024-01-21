package eternal.baseball.dto.team;

import eternal.baseball.domain.Team;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateTeamDTO {

    @NotEmpty
    private String teamName;
    @Size(min = 2, max = 5)
    @NotEmpty
    private String teamCode;
    @NotEmpty
    private String mainPositionEng;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;

    public Team toEntity() {
        return Team.builder()
                .teamName(teamName)
                .teamCode(teamCode)
                .build();
    }
}
