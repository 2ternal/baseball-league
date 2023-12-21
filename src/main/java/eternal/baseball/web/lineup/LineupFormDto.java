package eternal.baseball.web.lineup;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LineupFormDto {

    private ArrayList<PlayerForm> startingPlayers;
    private ArrayList<PlayerForm> benchPlayers;

    public LineupFormDto() {
    }

    public LineupFormDto(ArrayList<PlayerForm> startingPlayers, ArrayList<PlayerForm> benchPlayers) {
        this.startingPlayers = startingPlayers;
        this.benchPlayers = benchPlayers;
    }

    public LineupFormDto(ArrayList<TeamMember> teamMembers) {
        this.startingPlayers = writeStartingPlayer(teamMembers.subList(0, 9));
        this.benchPlayers = writeBenchPlayer(teamMembers.subList(9, teamMembers.size()));
    }

    private ArrayList<PlayerForm> writeStartingPlayer(List<TeamMember> teamMembers) {
        ArrayList<PlayerForm> startingPlayers = new ArrayList<>();
        int order = 0;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            startingPlayers.add(new PlayerForm(teamMember, Position.fromOrder(order), order));
        }
        return startingPlayers;
    }

    private ArrayList<PlayerForm> writeBenchPlayer(List<TeamMember> teamMembers) {
        ArrayList<PlayerForm> benchPlayers = new ArrayList<>();
        int order = 9;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            benchPlayers.add(new PlayerForm(teamMember, Position.NONE, order));
        }
        return benchPlayers;
    }
}
