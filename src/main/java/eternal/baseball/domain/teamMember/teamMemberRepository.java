package eternal.baseball.domain.teamMember;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class teamMemberRepository {

    private static HashMap<Long, TeamMember> teamMemberRepository = new HashMap<>();
    private static Long sequence = 0L;

    //새 팀원 추가
    public TeamMember save(TeamMember teamMember) {
        teamMember.setTeamMemberId(++sequence);
        teamMemberRepository.put(teamMember.getMemberId(), teamMember);
        return teamMember;
    }

    //팀원 아이디로 teamMember 찾기
    public TeamMember findByTeamMemberId(Long teamMemberId) {
        return teamMemberRepository.get(teamMemberId);
    }

    //팀 아이디로 다수의 teamMember 찾기
    public List<TeamMember> findByLoginId(Long teamId) {
        return findAll().stream()
                .filter(m -> m.getTeamId().equals(teamId))
                .collect(Collectors.toList());
    }

    public List<TeamMember> findAll() {
        return new ArrayList<>(teamMemberRepository.values());
    }

    public void clearStore() {
        teamMemberRepository.clear();
    }
}
