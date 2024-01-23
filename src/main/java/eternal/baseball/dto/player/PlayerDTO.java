package eternal.baseball.dto.player;

import eternal.baseball.domain.TeamMember;
import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private TeamMemberDTO teamMember;
    private Position position;
    private Integer orderNum;

    public static PlayerDTO from(Player player) {
        return PlayerDTO.builder()
                .teamMember(TeamMemberDTO.from(player.getTeamMember()))
                .position(player.getPosition())
                .orderNum(player.getOrderNum())
                .build();
    }

    public static PlayerDTO fromTeamMember(TeamMemberDTO teamMember, Position position, Integer orderNum) {
        return PlayerDTO.builder()
                .teamMember(teamMember)
                .position(position)
                .orderNum(orderNum)
                .build();
    }
}
