package eternal.baseball.service;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberFormDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.repository.MemberRepository;
import eternal.baseball.repository.TeamMemberRepository;
import eternal.baseball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {

    public final TeamMemberRepository teamMemberRepository;
    public final TeamRepository teamRepository;
    public final MemberRepository memberRepository;

    /**
     * 새 팀원 추가
     */
    public ResponseDataDTO<TeamMemberDTO> joinTeamMember(TeamMemberFormDTO joinTeamMember, MemberDTO loginMember) {

        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        // 등번호 중복 검증
        if (teamMemberRepository.findByTeamCode(joinTeamMember.getTeamCode()).stream()
                .anyMatch(tm -> tm.getBackNumber().equals(joinTeamMember.getBackNumber()))) {
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

        Member member = memberRepository.findByMemberId(loginMember.getMemberId());
        Team team = teamRepository.findByTeamCode(joinTeamMember.getTeamCode());
        TeamMember teamMember = teamMemberRepository.save(joinTeamMember.toEntity(member, team));

        log.info("[joinTeamMember service] teamMember={}", teamMember);

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
    public TeamMemberDTO findTeamMember(Long teamMemberId) {
        return TeamMemberDTO.from(teamMemberRepository.findByTeamMemberId(teamMemberId));
    }

    public TeamMemberDTO findTeamMember(Long memberId, String teamCode) {
        return TeamMemberDTO.from(teamMemberRepository.findByMemberIdTeamCode(memberId, teamCode));
    }

    /**
     * 멤버 ID로 다수의 teamMember 찾기
     */
    public List<TeamMemberDTO> findByMemberId(Long memberId) {
        return teamMemberRepository.findByMemberId(memberId).stream()
                .map(TeamMemberDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 멤버 이름으로 다수의 teamMember 찾기
     */
    public List<TeamMemberDTO> findByMemberName(String memberName) {
        return teamMemberRepository.findByMemberName(memberName).stream()
                .map(TeamMemberDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 팀 Id로 다수의 teamMember 찾기
     */
    public ArrayList<TeamMemberDTO> findTeamMembers(String teamCode) {
        return teamMemberRepository.findByTeamCode(teamCode).stream()
                .map(TeamMemberDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
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
