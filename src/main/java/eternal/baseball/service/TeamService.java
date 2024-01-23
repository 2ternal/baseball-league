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
import java.util.Optional;
import java.util.stream.Collectors;

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

        return ResponseDataDTO.<TeamDTO>builder()
                .error(false)
                .data(TeamDTO.from(team))
                .build();
    }

    /**
     * 팀 정보 수정
     */
    public Team editTeam(Long teamId, Team team) {
        return teamRepository.edit(teamId, team);
    }

    /**
     * 구단주 교체
     */
    public ResponseDataDTO<TeamDTO> changeOwner(String teamCode, MemberDTO newOwnerDTO) {

        List<BindingErrorDTO> bindingErrors = new ArrayList<>();

        Optional<Long> teamId = teamRepository.findTeamIdByTeamCode(teamCode);

        if (teamId.isEmpty()) {
            BindingErrorDTO bindingError = BindingErrorDTO.builder()
                    .errorCode("missMatchTeamCode")
                    .errorMessage("일치하는 팀 코드가 존재하지 않습니다")
                    .build();

            bindingErrors.add(bindingError);

            return ResponseDataDTO.<TeamDTO>builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        Team team = teamRepository.findByTeamId(teamId.get());
        team.setOwner(memberRepository.findByMemberId(newOwnerDTO.getMemberId()));

        teamRepository.edit(team.getTeamId(), team);

        return ResponseDataDTO.<TeamDTO>builder()
                .error(false)
                .data(TeamDTO.from(team))
                .build();
    }

    /**
     * 팀 코드로 team 찾기
     */
    public TeamDTO findTeam(String teamCode) {
        Team team = teamRepository.findAll().stream()
                .filter(t -> t.getTeamCode().equals(teamCode))
                .findFirst()
                .orElse(null);

        if (team == null) {
            return null;
        }

        return TeamDTO.from(team);
    }

    /**
     * 모든 team 찾기
     */
    public List<TeamDTO> findTeams() {
        return teamRepository.findAll().stream()
                .map(TeamDTO::from)
                .collect(Collectors.toList());
    }
}
