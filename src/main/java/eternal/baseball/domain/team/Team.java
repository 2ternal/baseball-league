package eternal.baseball.domain.team;

import eternal.baseball.domain.member.Member;
import eternal.baseball.web.team.CreateTeamForm;
import lombok.Data;

@Data
public class Team {

    private Long teamId;
    private String teamName;
    private String teamCode;
    private Member owner;

    public Team() {
    }

    public Team(String teamName, String teamCode, Member member) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.owner = member;
    }

    public void createTeamFormToTeam(CreateTeamForm createTeamForm) {
        this.teamName = createTeamForm.getTeamName();
        this.teamCode = createTeamForm.getTeamCode();
    }
}
