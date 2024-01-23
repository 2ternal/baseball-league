package eternal.baseball.dto.lineup;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.Lineup;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.player.PlayerDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LineupFormDTO {

    private String lineupName;
    private ArrayList<PlayerDTO> startingPlayers;
    private ArrayList<PlayerDTO> benchPlayers;

    public LineupFormDTO() {
    }

    public LineupFormDTO(Lineup lineup) {
        this.lineupName = lineup.getLineupName();
        this.startingPlayers = lineup.getStarting().stream()
                .map(PlayerDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
        this.benchPlayers = lineup.getBench().stream()
                .map(PlayerDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public LineupFormDTO(ArrayList<PlayerDTO> startingPlayers, ArrayList<PlayerDTO> benchPlayers) {
        this.startingPlayers = startingPlayers;
        this.benchPlayers = benchPlayers;
    }

    public LineupFormDTO(ArrayList<TeamMember> teamMembers) {
        this.startingPlayers = writeStartingPlayer(teamMembers.subList(0, 9));
        this.benchPlayers = writeBenchPlayer(teamMembers.subList(9, teamMembers.size()));
    }

    private ArrayList<PlayerDTO> writeStartingPlayer(List<TeamMember> teamMembers) {
        ArrayList<PlayerDTO> startingPlayers = new ArrayList<>();
        int order = 0;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            startingPlayers.add(PlayerDTO.fromTeamMember(TeamMemberDTO.from(teamMember), Position.fromOrder(order), order));
        }
        return startingPlayers;
    }

    private ArrayList<PlayerDTO> writeBenchPlayer(List<TeamMember> teamMembers) {
        ArrayList<PlayerDTO> benchPlayers = new ArrayList<>();
        int order = 9;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            benchPlayers.add(PlayerDTO.fromTeamMember(TeamMemberDTO.from(teamMember), Position.NONE, order));
        }
        return benchPlayers;
    }
}
