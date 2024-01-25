package eternal.baseball.controller;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.CreateTeamDTO;
import eternal.baseball.dto.team.TeamDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberFormDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.global.extension.ControllerUtil;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final ControllerUtil controllerUtil;

    /**
     * 팀 목록 페이지
     */
    @GetMapping("/teams")
    public String teams(Model model) {
        List<TeamDTO> teams = teamService.findTeams();
        model.addAttribute("teams", teams);
        return "team/teams";
    }

    /**
     * 팀 창단 페이지
     */
    @GetMapping("/createTeam")
    public String createTeamForm(Model model) {
        model.addAttribute("createTeamForm", new CreateTeamDTO());
        return "team/createTeamForm";
    }

    /**
     * 팀 창단
     */
    @PostMapping("/createTeam")
    public String createTeam(@Validated @ModelAttribute("createTeamForm") CreateTeamDTO createTeamDTO,
                             BindingResult bindingResult,
                             HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("[createTeam] bindingResult={}", bindingResult);
            return "team/createTeamForm";
        }

        MemberDTO loginMember = controllerUtil.getLoginMember(request);
        log.info("[createTeam] loginMember={}", loginMember);
        log.info("[createTeam] createTeamDTO={}", createTeamDTO);

        // 팀 생성
        ResponseDataDTO<TeamDTO> response = teamService.createTeam(createTeamDTO, loginMember);

        if (response.isError()) {
            controllerUtil.convertBindingError(bindingResult, response.getBindingErrors());
            log.info("[createTeam] bindingResult={}", bindingResult);
            return "team/createTeamForm";
        }

        TeamDTO teamDTO = response.getData();

        TeamMemberFormDTO teamMemberFormDTO = TeamMemberFormDTO.builder()
                .teamName(teamDTO.getTeamName())
                .teamCode(teamDTO.getTeamCode())
                .memberName(loginMember.getName())
                .mainPosition(createTeamDTO.getMainPosition())
                .backNumber(createTeamDTO.getBackNumber())
                .teamMemberShip(TeamMemberShip.OWNER.getDescription())
                .build();
        log.info("[createTeam] teamMember={}", teamMemberFormDTO);

        // 구단주로 팀에 가입
        teamMemberService.joinTeamMember(teamMemberFormDTO, loginMember);

        return "redirect:/team/teams";
    }

    /**
     * 팀 상세 페이지
     */
    @GetMapping("/{teamCode}")
    public String team(@PathVariable String teamCode, Model model, HttpServletRequest request) {

        TeamDTO team = teamService.findTeam(teamCode);

        List<TeamMemberDTO> teamMembers = teamMemberService.findTeamMembers2(teamCode);
        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        log.info("[team] loginMember={}", loginMemberDTO);

        TeamMemberDTO loginTeamMember = teamMemberService.findTeamMember2(loginMemberDTO.getMemberId(), teamCode);
        log.info("[team] loginTeamMember={}", loginTeamMember);

        if (loginTeamMember != null && loginTeamMember.getMemberShip().getGrade() < TeamMemberShip.COACH.getGrade()) {
            model.addAttribute("manage", true);
            log.info("[team] can manage!!");
        }

        model.addAttribute("team", team);
        model.addAttribute("teamMembers", teamMembers);

        return "team/team";
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return List.of(Position.values());
    }
}
