package eternal.baseball.service;

import eternal.baseball.domain.Team;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.CreateTeamDTO;
import eternal.baseball.dto.team.TeamDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.repository.MemberRepository;
import eternal.baseball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TeamService {

    public final MemberRepository memberRepository;
    public final TeamRepository teamRepository;

    /**
     * 새 팀 추가
     */
    public ResponseDataDTO<TeamDTO> createTeam(CreateTeamDTO createTeam, MemberDTO owner) {

        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        // 팀명 중복 검증
        if (!ObjectUtils.isEmpty(teamRepository.findByTeamName(createTeam.getTeamName()))) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("teamName")
                    .errorCode("duplicateTeamName")
                    .errorMessage("중복되는 팀명 입니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }
        // 팀 코드 중복 검증
        if (!ObjectUtils.isEmpty(teamRepository.findByTeamCode(createTeam.getTeamCode()))) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("teamCode")
                    .errorCode("duplicateTeamCode")
                    .errorMessage("중복되는 팀 코드 입니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }
        if (!bindingErrors.isEmpty()) {
            return ResponseDataDTO.<TeamDTO>builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        Team team = createTeam.toEntity();
        team.setOwner(memberRepository.findByMemberId(owner.getMemberId()));

        teamRepository.save(team);

        //teamMember 추가



        return ResponseDataDTO.<TeamDTO>builder()
                .data(TeamDTO.builder()
                        .teamName(team.getTeamName())
                        .teamCode(team.getTeamCode())
                        .owner(owner)
                        .build())
                .build();
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
    public Team findTeam2(Long teamId) {
        return teamRepository.findByTeamId(teamId);
    }

    public TeamDTO findTeam1(Long teamId) {
        Team team = teamRepository.findByTeamId(teamId);
        return new TeamDTO(team);
    }

    public TeamDTO findTeam(String teamCode) {
        Team team = teamRepository.findAll().stream()
                .filter(t -> t.getTeamCode().equals(teamCode))
                .findFirst()
                .orElse(null);

        if (team == null) {
            return null;
        }
        return TeamDTO.builder()
                .owner(MemberDTO.builder()
                        .memberId(team.getOwner().getMemberId())
                        .loginId(team.getOwner().getLoginId())
                        .name(team.getOwner().getName())
                        .birthday(team.getOwner().getBirthday())
                        .build())
                .teamCode(teamCode)
                .teamName(team.getTeamName())
                .build();
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
