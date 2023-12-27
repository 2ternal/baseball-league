package eternal.baseball.domain.teamMember;

import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TeamMemberRepository {

    private static final HashMap<Long, TeamMember> teamMemberRepository = new HashMap<>();
    private static Long sequence = 0L;

    /**
     * 새 팀원 추가
     */
    public TeamMember save(TeamMember teamMember) {
        teamMember.setTeamMemberId(++sequence);
        teamMemberRepository.put(teamMember.getTeamMemberId(), teamMember);
        return teamMember;
    }

    /**
     * 팀원 ID로 teamMember 찾기
     */
    public TeamMember findByTeamMemberId(Long teamMemberId) {
        return teamMemberRepository.get(teamMemberId);
    }

    /**
     * 팀 ID로 다수의 teamMember 찾기
     */
    public List<TeamMember> findByTeamId(Long teamId) {
        return findAll().stream()
                .filter(m -> m.getTeam().getTeamId().equals(teamId))
                .collect(Collectors.toList());
    }

    /**
     * 멤버 이름으로 다수의 teamMember 찾기
     */
    public List<TeamMember> findByMemberName(String memberName) {
        return findAll().stream()
                .filter(m -> m.getMember().getName().equals(memberName))
                .collect(Collectors.toList());
    }

    /**
     * 팀 이름으로 다수의 teamMember 찾기
     */
    public ArrayList<TeamMember> findByTeamName(String teamName) {
        return (ArrayList<TeamMember>) findAll().stream()
                .filter(m -> m.getTeam().getTeamName().equals(teamName))
                .collect(Collectors.toList());
    }

    /**
     * 멤버의 팀 가입 여부 체크
     */
    public Boolean teamMemberCheck(Member member, Team team) {
        return findAll().stream()
                .filter(m -> m.getMember().equals(member))
                .filter(m -> m.getTeam().equals(team))
                .findFirst()
                .isEmpty();
    }

    public List<TeamMember> findAll() {
        return new ArrayList<>(teamMemberRepository.values());
    }

    public void clearStore() {
        teamMemberRepository.clear();
    }
}
