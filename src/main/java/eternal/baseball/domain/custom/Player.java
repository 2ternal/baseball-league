package eternal.baseball.domain.custom;

import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

@Data
public class Player {

    private TeamMember teamMember;
    private Position position;
    private Integer orderNum;

    public Player() {
    }

    public Player(TeamMember teamMember) {
        this.teamMember = teamMember;
        this.position = Position.NONE;
        this.orderNum = 0;
    }

    public Player(TeamMember teamMember, Position position, Integer battingOrder) {
        this.teamMember = teamMember;
        this.position = position;
        this.orderNum = battingOrder;
    }
}
