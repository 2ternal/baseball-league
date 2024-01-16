package eternal.baseball.web.lineup;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.lineup.Lineup;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LineupFormDto {

    private String lineupName;
    private ArrayList<PlayerDto> startingPlayers;
    private ArrayList<PlayerDto> benchPlayers;

    public LineupFormDto() {
    }

    public LineupFormDto(Lineup lineup) {
        this.lineupName = lineup.getLineupName();
        this.startingPlayers = lineup.getStarting().stream()
                .map(PlayerDto::new)
                .collect(Collectors.toCollection(ArrayList::new));
        this.benchPlayers = lineup.getBench().stream()
                .map(PlayerDto::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public LineupFormDto(ArrayList<PlayerDto> startingPlayers, ArrayList<PlayerDto> benchPlayers) {
        this.startingPlayers = startingPlayers;
        this.benchPlayers = benchPlayers;
    }

    public LineupFormDto(ArrayList<TeamMember> teamMembers) {
        this.startingPlayers = writeStartingPlayer(teamMembers.subList(0, 9));
        this.benchPlayers = writeBenchPlayer(teamMembers.subList(9, teamMembers.size()));
    }

    private ArrayList<PlayerDto> writeStartingPlayer(List<TeamMember> teamMembers) {
        ArrayList<PlayerDto> startingPlayers = new ArrayList<>();
        int order = 0;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            startingPlayers.add(new PlayerDto(teamMember, Position.fromOrder(order), order));
        }
        return startingPlayers;
    }

    private ArrayList<PlayerDto> writeBenchPlayer(List<TeamMember> teamMembers) {
        ArrayList<PlayerDto> benchPlayers = new ArrayList<>();
        int order = 9;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            benchPlayers.add(new PlayerDto(teamMember, Position.NONE, order));
        }
        return benchPlayers;
    }
}
