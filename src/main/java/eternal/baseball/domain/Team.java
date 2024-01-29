package eternal.baseball.domain;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class Team {

    private Long teamId;
    private String teamName;
    private String teamCode;
    private Member owner;

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    @Builder
    public Team(Long teamId, String teamName, String teamCode, Member owner) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.owner = owner;
    }
}
