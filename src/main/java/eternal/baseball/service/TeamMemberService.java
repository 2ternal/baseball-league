package eternal.baseball.service;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.teamMember.RequestTeamMemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.repository.MemberRepository;
import eternal.baseball.repository.TeamMemberRepository;
import eternal.baseball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    public final TeamMemberRepository teamMemberRepository;
    public final TeamRepository teamRepository;
    public final MemberRepository memberRepository;
    public final TeamService teamService;

    /**
     * 새 팀원 추가
     */
    public ResponseDataDTO<TeamMemberDTO> joinTeamMember(RequestTeamMemberDTO requestMember) {

        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        // 등번호 중복 검증
        if (teamMemberRepository.findByTeamCode(requestMember.getTeamCode()).stream()
                .anyMatch(tm -> tm.getBackNumber().equals(requestMember.getBackNumber()))) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("backNumber")
                    .errorCode("sameBackNumber")
                    .errorMessage("이미 사용중인 번호입니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }

        if (!bindingErrors.isEmpty()) {
            return ResponseDataDTO.<TeamMemberDTO>builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        Member member = memberRepository.findByMemberId(requestMember.getMember().getMemberId());
        Team team = teamRepository.findByTeamCode(requestMember.getTeamCode());
        TeamMember teamMember = teamMemberRepository.save(requestMember.toEntity(member, team));

        TeamMemberDTO teamMemberDTO = TeamMemberDTO.from(teamMember);

        return ResponseDataDTO.<TeamMemberDTO>builder()
                .error(false)
                .data(teamMemberDTO)
                .build();
    }

    /**
     * 팀원 정보 수정
     */
    public TeamMemberDTO editTeamMember(TeamMemberDTO teamMember) {
        Team team = teamRepository.findByTeamCode(teamMember.getTeam().getTeamCode());
        Member member = memberRepository.findByMemberId(teamMember.getMember().getMemberId());

        TeamMember editTeamMember = teamMember.toEntity(team, member);
        teamMemberRepository.edit(teamMember.getTeamMemberId(), editTeamMember);

        return TeamMemberDTO.from(editTeamMember);
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

    public TeamMember findTeamMember(Long memberId, String teamCode) {
        return teamMemberRepository.findByMemberIdTeamCode(memberId, teamCode);
    }

    public TeamMemberDTO findTeamMember2(Long teamMemberId) {
        return TeamMemberDTO.from(teamMemberRepository.findByTeamMemberId(teamMemberId));
    }

    public TeamMemberDTO findTeamMember2(Long memberId, String teamCode) {
        return TeamMemberDTO.from(teamMemberRepository.findByMemberIdTeamCode(memberId, teamCode));
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

    public ArrayList<TeamMember> findTeamMembers(String teamCode) {
        return teamMemberRepository.findByTeamCode(teamCode);
    }

    public List<TeamMemberDTO> findTeamMembers2(String teamCode) {
        return teamMemberRepository.findByTeamCode(teamCode).stream()
                .map(TeamMemberDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 멤버의 팀 가입 여부 체크
     */
    public Boolean joinTeamMemberCheck(Long memberId, String teamCode) {
        return teamMemberRepository.findAll().stream()
                .filter(teamMember -> teamMember.getMember().getMemberId().equals(memberId))
                .anyMatch(teamMember -> teamMember.getTeam().getTeamCode().equals(teamCode));
    }

    /**
     * 등번호 중복 여부 체크
     */
    public Boolean duplicateBackNumberCheck(Long teamId, Long backNumber) {
        return teamMemberRepository.findByTeamId(teamId).stream()
                .anyMatch(tm -> tm.getBackNumber().equals(backNumber));
    }
}