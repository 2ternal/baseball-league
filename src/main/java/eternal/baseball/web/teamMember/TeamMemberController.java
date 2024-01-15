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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/teamMember")
public class TeamMemberController {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 팀원 상세 페이지
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

        Boolean joinCheck = !teamMemberRepository.teamMemberCheck(loginMember.getMemberId(), joinTeam.getTeamId());
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

        return "teamMember/joinTeamMemberForm";
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
            return "teamMember/joinTeamMemberForm";
        }

        log.info("[joinTeamV2] joinTeamMemberDto={}", joinTeamMemberDto);

        Member loginMember = getLoginMember(request);
        Team joinTeam = teamRepository.findByTeamId(teamId);

        Boolean joinCheck = !teamMemberRepository.teamMemberCheck(loginMember.getMemberId(), joinTeam.getTeamId());
        log.info("[joinTeamV2] joinCheck={}", joinCheck);
        if (joinCheck) {
            log.info("[joinTeamV2] 이미 팀에 가입된 선수");
            return "redirect:/team/" + teamId;
        }

        Optional<TeamMember> sameBackNumber = teamMemberRepository.findByTeamId(teamId).stream()
                .filter(tm -> tm.getBackNumber().equals(joinTeamMemberDto.getBackNumber()))
                .findFirst();

        if (sameBackNumber.isPresent()) {
            bindingResult.rejectValue("backNumber", "sameBackNumber", "이미 사용중인 번호입니다");
            return "teamMember/joinTeamMemberForm";
        }

        TeamMember teamMember = new TeamMember(loginMember, joinTeam, joinTeamMemberDto);
        teamMemberRepository.save(teamMember);
        log.info("[joinTeamV2] teamMember={}", teamMember);

        Long teamMemberId = teamMember.getTeamMemberId();

        return "redirect:/teamMember/" + teamMemberId;
    }

    /**
     * 관리자의 팀원 수정 페이지
     */
    @GetMapping("{teamMemberId}/manage")
    public String manageTeamMemberForm(@PathVariable Long teamMemberId,
                                       Model model,
                                       HttpServletRequest request) {

        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId);
        Member loginMember = getLoginMember(request);

        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamMember.getTeam().getTeamId());

        Integer loginTeamMemberGrade = loginTeamMember.getMemberShip().getGrade();

        log.info("[manageTeamMemberForm] teamMember != loginTeamMember ={}", teamMember != loginTeamMember);

        if (teamMember != loginTeamMember && loginTeamMemberGrade > teamMember.getMemberShip().getGrade()) {
            String redirectURI = "/team/" + teamMember.getTeam().getTeamId();
            AlertMessage message = new AlertMessage("수정하려는 대상의 권한이 더 높습니다", redirectURI);
            model.addAttribute("message", message);
            log.info("[manageTeamMemberForm] AlertMessage redirectURI={}", redirectURI);
            return "template/alert";
        }

        List<TeamMemberShip> teamMemberShips = new ArrayList<>();

        if (teamMember.getMemberShip().getGrade() == 1) {
            teamMemberShips.add(TeamMemberShip.OWNER);
        } else {
            teamMemberShips = getTeamMemberShips(loginTeamMemberGrade);
        }

        EditTeamMemberDto editTeamMemberDto = new EditTeamMemberDto(teamMember);

        log.info("[manageTeamMemberForm] teamMemberShips={}", teamMemberShips);

        model.addAttribute("teamMember", teamMember);
        model.addAttribute("teamMemberShips", teamMemberShips);
        model.addAttribute("editTeamMemberDto", editTeamMemberDto);

        return "teamMember/editTeamMemberForm";
    }

    /**
     * 관리자의 팀원 수정
     */
    @PostMapping("{teamMemberId}/manage")
    public String manageTeamMember(@PathVariable Long teamMemberId,
                                   @Validated @ModelAttribute("editTeamMemberDto") EditTeamMemberDto editTeamMemberDto,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   Model model) {

        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId);
        Member loginMember = getLoginMember(request);

        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamMember.getTeam().getTeamId());

        if (bindingResult.hasErrors()) {
            Integer loginMemberGrade = loginTeamMember.getMemberShip().getGrade();
            List<TeamMemberShip> teamMemberShips = getTeamMemberShips(loginMemberGrade);

            model.addAttribute("teamMember", teamMember);
            model.addAttribute("teamMemberShips", teamMemberShips);
            log.info("[manageTeamMember] has field error");
            return "teamMember/editTeamMemberForm";
        }

        TeamMemberShip editTeamMemberShip = TeamMemberShip.fromDescription(editTeamMemberDto.getTeamMemberShip());

        //수정하려는 등급이 감독 이상일 때
        //기존 감독이나 오너의 등급을 선수로 내린다
        if (teamMember.getMemberShip() != editTeamMemberShip && editTeamMemberShip.getGrade() <= TeamMemberShip.MANAGER.getGrade()) {
            TeamMember highTeamMember = teamMemberRepository.findByTeamId(teamMember.getTeam().getTeamId()).stream()
                    .filter(tm -> tm.getMemberShip().equals(editTeamMemberShip))
                    .findFirst()
                    .orElse(null);

            if (highTeamMember != null) {
                if (editTeamMemberShip.getGrade().equals(TeamMemberShip.OWNER.getGrade())) {
                    Team team = teamMember.getTeam();
                    team.setOwner(teamMember.getMember());
                    teamRepository.edit(teamMember.getTeam().getTeamId(), team);
                    log.info("[manageTeamMember] change Owner={}", team.getOwner());
                }

                highTeamMember.setMemberShip(TeamMemberShip.PLAYER);
                teamMemberRepository.edit(highTeamMember.getTeamMemberId(), highTeamMember);
                log.info("[manageTeamMember] change teamMemberShip={}", highTeamMember.getMember().getName());
            }
        }

        TeamMember editTeamMember = new TeamMember(teamMember, editTeamMemberDto);
        teamMemberRepository.edit(teamMemberId, editTeamMember);
        log.info("[manageTeamMember] edit teamMember={}", editTeamMember);

        return "redirect:/teamMember/" + teamMemberId;
    }

    private static List<TeamMemberShip> getTeamMemberShips(Integer loginMemberGrade) {
        List<TeamMemberShip> teamMemberShips = new ArrayList<>();
        for (int i = 4; i >= loginMemberGrade; i--) {
            teamMemberShips.add(TeamMemberShip.fromGrade(i));
        }
        return teamMemberShips;
    }

    private static Member getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return List.of(Position.values());
    }
}
