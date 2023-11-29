package eternal.baseball.domain.custom;

import lombok.Data;

@Data
public class Player {

    private Long teamMemberId;
    private Position position;

    public Player() {
    }

    public Player(Long teamMemberId, Position position) {
        this.teamMemberId = teamMemberId;
        this.position = position;
    }
}
