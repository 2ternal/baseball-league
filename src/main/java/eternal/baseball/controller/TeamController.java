package eternal.baseball.controller;

import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import eternal.baseball.global.constant.SessionConst;
import eternal.baseball.dto.team.CreateTeamForm;
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
        List<Team> teams = teamService.findTeams();
        model.addAttribute("teams", teams);
        return "team/teams";
    }

    /**
     * 팀 창단 페이지
     */
    @GetMapping("/createTeam")
    public String createTeamForm(Model model) {
        model.addAttribute("createTeamForm", new CreateTeamForm());
        return "team/createTeamForm";
    }

    /**
     * 팀 창단
     */
    @PostMapping("/createTeam")
    public String createTeam(@Validated @ModelAttribute("createTeamForm") CreateTeamForm createTeamForm,
                             BindingResult bindingResult,
                             HttpServletRequest request) {

        // 팀명 중복 검증
        if (teamService.duplicateTeamNameCheck(createTeamForm.getTeamName())) {
            log.info("[createTeam] duplicateTeamName={}", createTeamForm.getTeamName());
            bindingResult.rejectValue("teamName", "duplicateTeamName", "중복되는 팀명 입니다");
        }

        // 팀 코드 중복 검증
        if (teamService.duplicateTeamCodeCheck(createTeamForm.getTeamCode())) {
            log.info("[createTeam] duplicateTeamCode={}", createTeamForm.getTeamCode());
            bindingResult.rejectValue("teamCode", "duplicateTeamCode", "중복되는 팀 코드 입니다");
        }

        if (bindingResult.hasErrors()) {
            log.info("[createTeam] bindingResult={}", bindingResult);
            return "team/createTeamForm";
        }

        Member loginMember = getLoginMember(request);
        log.info("[createTeam] loginMember={}", loginMember);

        //검증 후 팀 창단
        Team team = new Team();
        team.createTeamFormToTeam(createTeamForm);
        team.setOwner(loginMember);
        log.info("[createTeam] owner={}", team.getOwner());

        teamService.createTeam(team);
        log.info("[createTeam] team={}", team);

        //팀원에 추가
        TeamMember teamMember = new TeamMember(loginMember, team, TeamMemberShip.OWNER);

        teamMemberService.joinTeamMember(teamMember);
        log.info("[createTeam] teamMember={}", teamMember);

        return "redirect:/team/teams";
    }

    /**
     * 팀 상세 페이지
     */
    @GetMapping("/{teamId}")
    public String team(@PathVariable Long teamId, Model model, HttpServletRequest request) {

        Team team = teamService.findTeam(teamId);
        List<TeamMember> teamMembers = teamMemberService.findTeamMembers(teamId);

        Member loginMember = getLoginMember(request);
        log.info("[team] loginMember={}", loginMember);

        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMember.getMemberId(), teamId);

        log.info("[team] loginTeamMember={}", loginTeamMember);
        if (loginTeamMember != null && loginTeamMember.getMemberShip().getGrade() < TeamMemberShip.COACH.getGrade()) {
            model.addAttribute("manage", true);
            log.info("[team] can manage!!");
        }

        model.addAttribute("team", team);
        model.addAttribute("teamMembers", teamMembers);

        return "team/team";
    }

    private static Member getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
