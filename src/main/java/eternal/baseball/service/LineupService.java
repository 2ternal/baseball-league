package eternal.baseball.service;

import eternal.baseball.domain.Lineup;
import eternal.baseball.repository.LineupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineupService {

    private final LineupRepository lineupRepository;

    /**
     * 새 라인업 추가
     */
    public Lineup createLineup(Lineup lineup) {
        return lineupRepository.save(lineup);
    }

    /**
     * 라인업 정보 수정
     */
    public Lineup editLineup(Long lineupId, Lineup lineup) {
        return lineupRepository.edit(lineupId, lineup);
    }

    /**
     * 라인업 아이디로 라인업 삭제
     */
    public Lineup deleteLineup(Long lineupId) {
        return lineupRepository.deleteLineup(lineupId);
    }

    /**
     * 라인업 찾기
     */
    public Lineup findLineup(Long lineupId) {
        return lineupRepository.findByLineupId(lineupId);
    }

    /**
     * teamId 로 라인업 리스트 찾기
     */
    public List<Lineup> findTeamLineupList(Long teamId) {
        return lineupRepository.findAll().stream()
                .filter(l -> l.getTeam().getTeamId().equals(teamId))
                .collect(Collectors.toList());
    }
}
