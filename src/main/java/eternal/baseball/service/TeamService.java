package eternal.baseball.service;

import eternal.baseball.domain.Team;
import eternal.baseball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    public final TeamRepository teamRepository;

    /**
     * 새 팀 추가
     */
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    /**
     * 팀 정보 수정
     */
    public Team editTeam(Long teamId, Team team) {
        return teamRepository.edit(teamId, team);
    }

    /**
     * 팀 ID로 team 찾기
     */
    public Team findTeam(Long teamId) {
        return teamRepository.findByTeamId(teamId);
    }

    /**
     * 모든 team 찾기
     */
    public List<Team> findTeams() {
        return teamRepository.findAll();
    }

    /**
     * 팀명 유무 검증
     */
    public Boolean duplicateTeamNameCheck(String teamName) {
        return !ObjectUtils.isEmpty(teamRepository.findByTeamName(teamName));
    }

    /**
     * 팀 코드 유무 검증
     */
    public Boolean duplicateTeamCodeCheck(String teamCode) {
        return !ObjectUtils.isEmpty(teamRepository.findByTeamCode(teamCode));
    }

}
