package eternal.baseball.dto.lineup;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.dto.player.PlayerDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LineupFormDTO {

    private String teamCode;
    private String lineupName;
    private ArrayList<PlayerDTO> startingPlayers;
    private ArrayList<PlayerDTO> benchPlayers;

    public static LineupFormDTO from(LineupDTO lineupDTO) {
        return LineupFormDTO.builder()
                .teamCode(lineupDTO.getTeam().getTeamCode())
                .lineupName(lineupDTO.getLineupName())
                .startingPlayers(lineupDTO.getStarting())
                .benchPlayers(lineupDTO.getBench())
                .build();
    }

    public static LineupFormDTO fromTeamMembers(ArrayList<TeamMemberDTO> teamMembers) {
        return LineupFormDTO.builder()
                .startingPlayers(writeStartingPlayer(teamMembers.subList(0, 9)))
                .benchPlayers(writeBenchPlayer(teamMembers.subList(9, teamMembers.size())))
                .build();
    }

    private static ArrayList<PlayerDTO> writeStartingPlayer(List<TeamMemberDTO> teamMembers) {
        ArrayList<PlayerDTO> startingPlayers = new ArrayList<>();
        int order = 0;
        for (TeamMemberDTO teamMember : teamMembers) {
            order += 1;
            startingPlayers.add(PlayerDTO.fromTeamMember(teamMember, Position.fromOrder(order), order));
        }
        return startingPlayers;
    }

    private static ArrayList<PlayerDTO> writeBenchPlayer(List<TeamMemberDTO> teamMembers) {
        ArrayList<PlayerDTO> benchPlayers = new ArrayList<>();
        int order = 9;
        for (TeamMemberDTO teamMember : teamMembers) {
            order += 1;
            benchPlayers.add(PlayerDTO.fromTeamMember(teamMember, Position.NONE, order));
        }
        return benchPlayers;
    }
}
