package eternal.baseball.dto.lineup;

import eternal.baseball.domain.Lineup;
import eternal.baseball.dto.player.PlayerDTO;
import eternal.baseball.dto.team.TeamDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LineupDTO {

    private Long lineupId;
    private TeamDTO team;
    private TeamMemberDTO writer;
    private String lineupName;
    private ArrayList<PlayerDTO> starting;
    private ArrayList<PlayerDTO> bench;
    private Date updateTime;

    public static LineupDTO from(Lineup lineup) {
        return LineupDTO.builder()
                .lineupId(lineup.getLineupId())
                .team(TeamDTO.from(lineup.getTeam()))
                .writer(TeamMemberDTO.from(lineup.getWriter()))
                .lineupName(lineup.getLineupName())
                .starting(lineup.getStarting().stream()
                        .map(PlayerDTO::from)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .bench(lineup.getBench().stream()
                        .map(PlayerDTO::from)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .updateTime(lineup.getUpdateTime())
                .build();
    }
}
