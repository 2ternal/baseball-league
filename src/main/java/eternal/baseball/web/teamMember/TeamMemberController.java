package eternal.baseball.web.teamMember;


import eternal.baseball.domain.custom.Position;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/teamMember")
public class TeamMemberController {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 팀원 페이지
     */
    @GetMapping("{teamMemberId}")
    public String teamMember(@PathVariable Long teamMemberId, Model model) {

        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId);
        model.addAttribute("teamMember", teamMember);

        return "teamMember/teamMember";
    }

    /**
     * 팀 가입 페이지
     */
    @GetMapping("/joinTeam/{teamId}")
    public String joinTeamForm(@PathVariable Long teamId, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Team joinTeam = teamRepository.findByTeamId(teamId);
        Position[] positions = Position.values();

        model.addAttribute("member", loginMember);
        model.addAttribute("team", joinTeam);
        model.addAttribute("joinTeamMemberForm", new JoinTeamMemberForm());
        model.addAttribute("positions", positions);

        return "teamMember/joinTeamMemberForm";
    }

    /**
     * 팀 가입
     */
    @PostMapping("/joinTeam/{teamId}")
    public String joinTeam(@PathVariable Long teamId,
                           @Validated @ModelAttribute("joinTeamMemberForm") JoinTeamMemberForm joinTeamMemberForm,
                           BindingResult bindingResult,
                           HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "teamMember/joinTeamMemberForm";
        }

        //toDo 메소드 줄이기
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Team joinTeam = teamRepository.findByTeamId(teamId);

        TeamMember teamMember = new TeamMember();
        teamMember.addTeamMember(loginMember, joinTeam, TeamMemberShip.PLAYER, joinTeamMemberForm);

        teamMemberRepository.save(teamMember);
        log.info("[joinTeam] joinTeamMember={}", teamMember);

        Long teamMemberId = teamMember.getTeamMemberId();

        return "redirect:/teamMember/" + teamMemberId;
    }
}
