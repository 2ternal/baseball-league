package eternal.baseball.controller;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.CreateTeamDTO;
import eternal.baseball.dto.team.TeamDTO;
import eternal.baseball.dto.teamMember.RequestTeamMemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import eternal.baseball.global.constant.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

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

        MemberDTO loginMember = getLoginMember(request);
        log.info("[createTeam] loginMember={}", loginMember);
        log.info("[createTeam] createTeamDTO={}", createTeamDTO);

        // 팀 생성
        ResponseDataDTO<TeamDTO> response = teamService.createTeam(createTeamDTO, loginMember);

        if (response.isError()) {
            for (BindingErrorDTO bindingError : response.getBindingErrors()) {
                if (bindingError.getErrorField().isEmpty()) {
                    bindingResult.reject(bindingError.getErrorCode(),
                            bindingError.getErrorMessage());
                } else {
                    bindingResult.rejectValue(bindingError.getErrorField(),
                            bindingError.getErrorCode(),
                            bindingError.getErrorMessage());
                }
            }
            log.info("[createTeam] bindingResult={}", bindingResult);
            return "team/createTeamForm";
        }

        TeamDTO teamDTO = response.getData();

        RequestTeamMemberDTO teamMemberDTO = RequestTeamMemberDTO.builder()
                .teamCode(teamDTO.getTeamCode())
                .member(loginMember)
                .teamMemberShip(TeamMemberShip.OWNER)
                .mainPositionEng(createTeamDTO.getMainPositionEng())
                .backNumber(createTeamDTO.getBackNumber())
                .build();

        // 구단주로 팀에 가입
        teamMemberService.joinTeamMember(teamMemberDTO);
        log.info("[createTeam] teamMember={}", teamMemberDTO);

        return "redirect:/team/teams";
    }

    /**
     * 팀 상세 페이지
     */
    @GetMapping("/{teamCode}")
    public String team(@PathVariable String teamCode, Model model, HttpServletRequest request) {

        TeamDTO team = teamService.findTeam(teamCode);

        List<TeamMemberDTO> teamMembers = teamMemberService.findTeamMembers2(teamCode);
        MemberDTO loginMemberDTO = getLoginMember(request);
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

    private static MemberDTO getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (MemberDTO) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return List.of(Position.values());
    }
}
