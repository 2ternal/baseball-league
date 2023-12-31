package eternal.baseball.domain.lineup;

import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Lineup {

    private Long lineupId;
    private Team team;
    private TeamMember writer;
    private String lineupName;
    private ArrayList<Player> lineupCard;
    private ArrayList<Player> bench;
    private Date updateTime;

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
