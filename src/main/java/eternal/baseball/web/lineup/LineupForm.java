package eternal.baseball.web.lineup;

import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.teamMember.TeamMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LineupForm {

    private ArrayList<Player> startingPlayers;
    private ArrayList<Player> benchPlayers;

    public LineupForm() {
    }

    public LineupForm(ArrayList<Player> startingPlayers, ArrayList<Player> benchPlayers) {
        this.startingPlayers = startingPlayers;
        this.benchPlayers = benchPlayers;
    }

    public LineupForm(ArrayList<TeamMember> teamMembers) {
        this.startingPlayers = writeStartingPlayer(teamMembers.subList(0, 9));
        this.benchPlayers = writeBenchPlayer(teamMembers.subList(9, teamMembers.size()));
    }

    private ArrayList<Player> writeStartingPlayer(List<TeamMember> teamMembers) {
        ArrayList<Player> startingPlayers = new ArrayList<>();
        int order = 0;
        for (TeamMember teamMember : teamMembers) {
            order += 1;
            startingPlayers.add(new Player(teamMember, Position.fromOrder(order), order));
        }
        return startingPlayers;
    }

    private ArrayList<Player> writeBenchPlayer(List<TeamMember> teamMembers) {
        return (ArrayList<Player>) teamMembers.stream()
                .map(Player::new)
                .collect(Collectors.toList());
    }
}
