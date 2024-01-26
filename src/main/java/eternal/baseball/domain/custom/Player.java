package eternal.baseball.domain.custom;

import eternal.baseball.domain.TeamMember;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class Player {

    private TeamMember teamMember;
    private Position position;
    private Integer orderNum;

    @Builder
    public Player(TeamMember teamMember, Position position, Integer orderNum) {
        this.teamMember = teamMember;
        this.position = position;
        this.orderNum = orderNum;
    }
}
