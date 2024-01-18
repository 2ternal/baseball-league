package eternal.baseball.repository;

import eternal.baseball.domain.Lineup;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class LineupRepository {

    private static final Map<Long, Lineup> lineupStore = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * 새 라인업 추가
     */
    public Lineup save(Lineup lineup) {
        lineup.setLineupId(++sequence);
        lineupStore.put(lineup.getLineupId(), lineup);
        return lineup;
    }

    /**
     * 라인업 정보 수정
     */
    public Lineup edit(Long lineupId, Lineup lineup) {
        lineupStore.put(lineupId, lineup);
        return lineup;
    }

    /**
     * 라인업 아이디로 lineup 찾기
     */
    public Lineup findByLineupId(Long lineupId) {
        return lineupStore.get(lineupId);
    }

    /**
     * teamId 로 lineup 찾기
     */
    public List<Lineup> findByTeamId(Long teamId) {
        return findAll().stream()
                .filter(l -> l.getTeam().getTeamId().equals(teamId))
                .collect(Collectors.toList());
    }

    /**
     * 라인업 아이디로 라인업 삭제
     */
    public Lineup deleteLineup(Long lineupId) {
        return lineupStore.remove(lineupId);
    }

    public List<Lineup> findAll() {
        return new ArrayList<>(lineupStore.values());
    }

    public void clearStore() {
        lineupStore.clear();
    }
}
