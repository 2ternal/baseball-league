package eternal.baseball.repository;

import eternal.baseball.domain.TeamMember;
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
     * 팀원 정보 수정
     */
    public TeamMember edit(Long teamMemberId, TeamMember teamMember) {
        teamMemberRepository.put(teamMemberId, teamMember);
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
    public ArrayList<TeamMember> findByTeamId(Long teamId) {
        return (ArrayList<TeamMember>) findAll().stream()
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
     * 멤버 ID로 다수의 teamMember 찾기
     */
    public List<TeamMember> findByMemberId(Long memberId) {
        return findAll().stream()
                .filter(m -> m.getMember().getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    /**
     * 멤버 ID와 팀 ID로 teamMember 찾기
     */
    public TeamMember findByMemberIdTeamId(Long memberId, Long teamId) {
        return findAll().stream()
                .filter(m -> m.getMember().getMemberId().equals(memberId))
                .filter(m -> m.getTeam().getTeamId().equals(teamId))
                .findFirst()
                .orElse(null);
    }

    public List<TeamMember> findAll() {
        return new ArrayList<>(teamMemberRepository.values());
    }

    public void clearStore() {
        teamMemberRepository.clear();
    }
}
