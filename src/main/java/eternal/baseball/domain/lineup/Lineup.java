package eternal.baseball.domain.lineup;

import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Lineup {

    private Long lineupId;
    private Team team;
    private TeamMember writer;
    private ArrayList<Player> lineupCard;
    private ArrayList<Player> bench;

    public Lineup() {
    }

    public Lineup(Team team, TeamMember writer,
                  ArrayList<Player> lineupCard, ArrayList<Player> bench) {
        this.team = team;
        this.writer = writer;
        this.lineupCard = lineupCard;
        this.bench = bench;
    }
}
