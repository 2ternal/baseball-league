package eternal.baseball.domain;

import eternal.baseball.domain.custom.Player;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;

@Getter
@ToString(exclude = {"writer", "starting", "bench"})
@NoArgsConstructor
public class Lineup {

    private Long lineupId;
    private Team team;
    private TeamMember writer;
    private String lineupName;
    private ArrayList<Player> starting;
    private ArrayList<Player> bench;
    private Date updateTime;

    public void setLineupId(Long lineupId) {
        this.lineupId = lineupId;
    }

    @Builder
    public Lineup(Long lineupId, Team team, TeamMember writer, String lineupName, ArrayList<Player> starting, ArrayList<Player> bench, Date updateTime) {
        this.lineupId = lineupId;
        this.team = team;
        this.writer = writer;
        this.lineupName = lineupName;
        this.starting = starting;
        this.bench = bench;
        this.updateTime = updateTime;
    }
}
