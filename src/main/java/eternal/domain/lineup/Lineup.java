package eternal.domain.lineup;

import eternal.domain.custom.Player;
import lombok.Data;

import java.util.HashMap;

@Data
public class Lineup {

    private Long lineupId;
    private HashMap<Integer, Player> lineupCard;

    public Lineup() {
    }

    public Lineup(Long lineupId, HashMap<Integer, Player> lineupCard) {
        this.lineupId = lineupId;
        this.lineupCard = lineupCard;
    }
}
