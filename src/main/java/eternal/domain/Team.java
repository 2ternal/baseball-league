package eternal.domain;

import lombok.Data;

@Data
public class Team {

    private Long teamId;
    private String teamName;

    public Team() {
    }

    public Team(Long teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
