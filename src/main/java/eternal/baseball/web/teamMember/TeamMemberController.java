package eternal.baseball.web.teamMember;


import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.team.TeamRepository;
import eternal.baseball.domain.teamMember.TeamMember;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import eternal.baseball.web.extension.AlertMessage;
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
    public String joinTeamFormV2(@PathVariable Long teamId, Model model, HttpServletRequest request) {

        Member loginMember = getLoginMember(request);
        Team joinTeam = teamRepository.findByTeamId(teamId);

        Boolean joinCheck = !teamMemberRepository.teamMemberCheck(loginMember, joinTeam);
        log.info("[joinTeamFormV2] joinCheck={}", joinCheck);
        if (joinCheck) {
            log.info("[joinTeamFormV2] 이미 팀에 가입된 선수");
            String redirectURI = "/team/" + teamId;
            AlertMessage message = new AlertMessage("이미 팀에 가입되어 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        JoinTeamMemberDto joinTeamMemberDto = new JoinTeamMemberDto(loginMember, joinTeam);
        model.addAttribute("joinTeamMemberDto", joinTeamMemberDto);

        return "teamMember/joinTeamMemberFormV2";
    }

    /**
     * 팀 가입
     */
    @PostMapping("/joinTeam/{teamId}")
    public String joinTeamV2(@PathVariable Long teamId,
                             @Validated @ModelAttribute("joinTeamMemberDto") JoinTeamMemberDto joinTeamMemberDto,
                             BindingResult bindingResult,
                             HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "teamMember/joinTeamMemberFormV2";
        }

        log.info("[joinTeamV2] joinTeamMemberDto={}", joinTeamMemberDto);

        Member loginMember = getLoginMember(request);
        Team joinTeam = teamRepository.findByTeamId(teamId);

        Boolean joinCheck = !teamMemberRepository.teamMemberCheck(loginMember, joinTeam);
        log.info("[joinTeamV2] joinCheck={}", joinCheck);
        if (joinCheck) {
            log.info("[joinTeamV2] 이미 팀에 가입된 선수");
            return "redirect:/team/" + teamId;
        }

        /**
         * Dto --> Entity
         * joinTeamMemberDto의 toTeamMember()에서 teamMember의 생성자를 불러 teamMember를 만든다
         * 괜찮은 방법인가?
         */
        TeamMember teamMember = joinTeamMemberDto.toTeamMember(loginMember, joinTeam);
        teamMemberRepository.save(teamMember);
        log.info("[joinTeamV2] teamMember={}", teamMember);

        Long teamMemberId = teamMember.getTeamMemberId();

        return "redirect:/teamMember/" + teamMemberId;
    }

    private static Member getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    //    @GetMapping("/joinTeam/{teamId}")
    public String joinTeamForm(@PathVariable Long teamId, Model model, HttpServletRequest request) {
        Member loginMember = getLoginMember(request);
        Team joinTeam = teamRepository.findByTeamId(teamId);
        Position[] positions = Position.values();

        model.addAttribute("member", loginMember);
        model.addAttribute("team", joinTeam);
        model.addAttribute("joinTeamMemberForm", new JoinTeamMemberForm());
        model.addAttribute("positions", positions);

        return "teamMember/joinTeamMemberForm";
    }

    //    @PostMapping("/joinTeam/{teamId}")
    public String joinTeam(@PathVariable Long teamId,
                           @Validated @ModelAttribute("joinTeamMemberForm") JoinTeamMemberForm joinTeamMemberForm,
                           BindingResult bindingResult,
                           HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "teamMember/joinTeamMemberForm";
        }

        Member loginMember = getLoginMember(request);
        Team joinTeam = teamRepository.findByTeamId(teamId);

        TeamMember teamMember = new TeamMember(loginMember, joinTeam, TeamMemberShip.PLAYER, joinTeamMemberForm);
        teamMemberRepository.save(teamMember);
        log.info("[joinTeam] joinTeamMember={}", teamMember);

        Long teamMemberId = teamMember.getTeamMemberId();

        return "redirect:/teamMember/" + teamMemberId;
    }
}
