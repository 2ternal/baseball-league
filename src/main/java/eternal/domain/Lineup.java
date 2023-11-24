package eternal.domain;

import lombok.Data;

import java.util.HashMap;

@Data
public class Lineup {

    private Long lineupId;
    private HashMap<Integer, Player> lineup;

    public Lineup() {
    }

    public Lineup(Long lineupId, HashMap<Integer, Player> lineup) {
        this.lineupId = lineupId;
        this.lineup = lineup;
    }
}
