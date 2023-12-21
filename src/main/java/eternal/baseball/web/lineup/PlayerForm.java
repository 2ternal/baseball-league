package eternal.baseball.web.lineup;

import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

@Data
public class PlayerForm {

    private String name;
    private String position;
    private Integer orderNum;

    public PlayerForm() {
    }

    public PlayerForm(Player player) {
        this.name = player.getTeamMember().getMember().getName();
        this.position = player.getPosition().description;
        this.orderNum = player.getBattingOrder();
    }

    public PlayerForm(TeamMember teamMember) {
        this.name = teamMember.getMember().getName();
        this.position = Position.NONE.description;
        this.orderNum = 0;
    }

    public PlayerForm(TeamMember teamMember, Position position, Integer battingOrder) {
        this.name = teamMember.getMember().getName();
        this.position = position.description;
        this.orderNum = battingOrder;
    }

    public PlayerForm(String name, String position, Integer orderNum) {
        this.name = name;
        this.position = position;
        this.orderNum = orderNum;
    }
}
