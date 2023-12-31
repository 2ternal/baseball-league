package eternal.baseball.domain.lineup;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class LineupRepository {

    private static final Map<Long, Lineup> lineupRepository = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * 새 라인업 추가
     */
    public Lineup save(Lineup lineup) {
        lineup.setLineupId(++sequence);
        lineupRepository.put(lineup.getLineupId(), lineup);
        return lineup;
    }

    /**
     * 라인업 아이디로 lineup 찾기
     */
    public Lineup findByLineupId(Long lineupId) {
        return lineupRepository.get(lineupId);
    }

    /**
     * teamId 로 lineup 찾기
     */
    public List<Lineup> findByTeamId(Long teamId) {
        return findAll().stream()
                .filter(l -> l.getTeam().getTeamId().equals(teamId))
                .collect(Collectors.toList());
    }

    public List<Lineup> findAll() {
        return new ArrayList<>(lineupRepository.values());
    }

    public void clearStore() {
        lineupRepository.clear();
    }
}
