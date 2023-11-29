package eternal.baseball.domain.team;

import eternal.baseball.web.team.CreateTeamForm;
import lombok.Data;

@Data
public class Team {

    private Long teamId;
    private String teamName;
    private String teamCode;

    public Team() {
    }

    public Team(Long teamId, String teamName, String teamCode) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamCode = teamCode;
    }

    public void createTeamFormToTeam(CreateTeamForm createTeamForm) {
        this.teamName = createTeamForm.getTeamName();
        this.teamCode = createTeamForm.getTeamCode();
    }
}
