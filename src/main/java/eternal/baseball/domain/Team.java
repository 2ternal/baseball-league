package eternal.baseball.domain;

import eternal.baseball.dto.team.CreateTeamDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    private Long teamId;
    private String teamName;
    private String teamCode;
    private Member owner;

    public Team(String teamName, String teamCode, Member member) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.owner = member;
    }

    public void createTeamFormToTeam(CreateTeamDTO createTeamDTO) {
        this.teamName = createTeamDTO.getTeamName();
        this.teamCode = createTeamDTO.getTeamCode();
    }
}
