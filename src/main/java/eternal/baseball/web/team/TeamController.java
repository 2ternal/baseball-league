package eternal.baseball.web.team;

import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.team.TeamRepository;
import eternal.baseball.domain.teamMember.TeamMember;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import eternal.baseball.web.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @GetMapping("/teams")
    public String teams(Model model) {
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("teams", teams);
        return "team/teams";
    }

    @GetMapping("/createTeam")
    public String createTeamForm(Model model) {
        model.addAttribute("createTeamForm", new CreateTeamForm());
        return "team/createTeamForm";
    }

    @PostMapping("/createTeam")
    public String createTeam(@Validated @ModelAttribute("createTeamForm") CreateTeamForm createTeamForm,
                             BindingResult bindingResult,
                             HttpServletRequest request) {

        // 팀명 중복 검증
        if (!ObjectUtils.isEmpty(teamRepository.findByTeamName(createTeamForm.getTeamName()))) {
            log.info("[createTeam] duplicateTeamName={}", createTeamForm.getTeamName());
            bindingResult.reject("duplicateTeamName", "중복되는 팀명 입니다");
            log.info("[createTeam] bindingResult={}", bindingResult);
        }

        // 팀 코드 중복 검증
        if (!ObjectUtils.isEmpty(teamRepository.findByTeamCode(createTeamForm.getTeamCode()))) {
            log.info("[createTeam] duplicateTeamCode={}", createTeamForm.getTeamCode());
            bindingResult.reject("duplicateTeamCode", "중복되는 팀 코드 입니다");
            log.info("[createTeam] bindingResult={}", bindingResult);
        }

        if (bindingResult.hasErrors()) {
            log.info("[createTeam] bindingResult={}", bindingResult);
            return "team/createTeamForm";
        }

        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        log.info("[createTeam] loginMember={}", loginMember);

        //검증 후 팀 창단
        Team team = new Team();
        team.createTeamFormToTeam(createTeamForm);
        team.setOwner(loginMember);
        log.info("[createTeam] owner={}", team.getOwner());

        teamRepository.save(team);
        log.info("[createTeam] team={}", team);

        //팀원에 추가
        TeamMember teamMember = new TeamMember();
        teamMember.addTeamMember(loginMember, team, TeamMemberShip.OWNER);

        teamMemberRepository.save(teamMember);
        log.info("[createTeam] teamMember={}", teamMember);

        return "redirect:teams";
    }
}
