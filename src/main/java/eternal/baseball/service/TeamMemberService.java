package eternal.baseball.service;

import eternal.baseball.domain.TeamMember;
import eternal.baseball.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    public final TeamMemberRepository teamMemberRepository;

    /**
     * 새 팀원 추가
     */
    public TeamMember joinTeamMember(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    /**
     * 팀원 정보 수정
     */
    public TeamMember editTeamMember(Long teamMemberId, TeamMember teamMember) {
        return teamMemberRepository.edit(teamMemberId, teamMember);
    }

    /**
     * 멤버 ID와 팀 ID로 teamMember 찾기
     */
    public TeamMember findTeamMember(Long teamMemberId) {
        return teamMemberRepository.findByTeamMemberId(teamMemberId);
    }

    public TeamMember findTeamMember(Long memberId, Long teamId) {
        return teamMemberRepository.findByMemberIdTeamId(memberId, teamId);
    }

    /**
     * 멤버 ID로 다수의 teamMember 찾기
     */
    public List<TeamMember> findByMemberId(Long memberId) {
        return teamMemberRepository.findByMemberId(memberId);
    }

    /**
     * 멤버 이름으로 다수의 teamMember 찾기
     */
    public List<TeamMember> findByMemberName(String memberName) {
        return teamMemberRepository.findByMemberName(memberName);
    }

    /**
     * 팀 Id로 다수의 teamMember 찾기
     */
    public ArrayList<TeamMember> findTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    /**
     * 멤버의 팀 가입 여부 체크
     */
    public Boolean joinTeamMemberCheck(Long memberId, Long teamId) {
        return teamMemberRepository.findAll().stream()
                .filter(m -> m.getMember().getMemberId().equals(memberId))
                .anyMatch(m -> m.getTeam().getTeamId().equals(teamId));
    }

    /**
     * 등번호 중복 여부 체크
     */
    public Boolean duplicateBackNumberCheck(Long teamId, Long backNumber) {
        return teamMemberRepository.findByTeamId(teamId).stream()
                .anyMatch(tm -> tm.getBackNumber().equals(backNumber));
    }
}
