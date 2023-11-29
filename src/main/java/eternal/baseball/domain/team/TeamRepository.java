package eternal.baseball.domain.team;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TeamRepository {

    private static Map<Long, Team> teamStore = new HashMap<>();
    private static Long sequence = 0L;

    //새 팀 추가
    public Team save(Team team) {
        team.setTeamId(++sequence);
        teamStore.put(team.getTeamId(), team);
        return team;
    }

    //팀명으로 team 찾기
    public Optional<Team> findByTeamName(String teamName) {
        return findAll().stream()
                .filter(m -> m.getTeamName().equals(teamName))
                .findFirst();
    }

    public List<Team> findAll() {
        return new ArrayList<>(teamStore.values());
    }

    public void clearStore() {
        teamStore.clear();
    }
}
