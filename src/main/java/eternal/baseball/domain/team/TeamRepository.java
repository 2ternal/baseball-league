package eternal.baseball.domain.team;

import eternal.baseball.domain.member.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TeamRepository {

    private static final Map<Long, Team> teamStore = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * 새 팀 추가
     */
    public Team save(Team team) {
        team.setTeamId(++sequence);
        teamStore.put(team.getTeamId(), team);
        return team;
    }

    /**
     * 팀 정보 수정
     */
    public Team edit(Long teamId, Team team) {
        teamStore.put(teamId, team);
        return team;
    }

    /**
     * 팀 ID로 team 찾기
     */
    public Team findByTeamId(Long teamId) {
        return teamStore.get(teamId);
    }

    /**
     * 팀명으로 team 찾기
     */
    public Optional<Team> findByTeamName(String teamName) {
        return findAll().stream()
                .filter(m -> m.getTeamName().equals(teamName))
                .findFirst();
    }

    /**
     * 팀 코드로 team 찾기
     */
    public Optional<Team> findByTeamCode(String teamCode) {
        return findAll().stream()
                .filter(m -> m.getTeamCode().equals(teamCode))
                .findFirst();
    }

    public List<Team> findAll() {
        return new ArrayList<>(teamStore.values());
    }

    public void clearStore() {
        teamStore.clear();
    }
}
