package eternal.baseball.controller;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.TeamDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberFormDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.global.extension.ControllerUtil;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import eternal.baseball.global.extension.AlertMessageBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/teamMember")
public class TeamMemberController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final ControllerUtil controllerUtil;

    /**
     * 팀원 상세 페이지
     */
    @GetMapping("{teamMemberId}")
    public String teamMember(@PathVariable Long teamMemberId, Model model) {

        TeamMemberDTO teamMember = teamMemberService.findTeamMember(teamMemberId);
        model.addAttribute("teamMember", teamMember);

        return "teamMember/teamMember";
    }

    /**
     * 팀 가입 페이지
     */
    @GetMapping("/joinTeam/{teamCode}")
    public String joinTeamForm(@PathVariable String teamCode, Model model, HttpServletRequest request) {

        MemberDTO loginMember = controllerUtil.getLoginMember(request);
        TeamDTO joinTeam = teamService.findTeam(teamCode);

        Boolean joinCheck = teamMemberService.joinTeamMemberCheck(loginMember.getMemberId(), joinTeam.getTeamCode());
        log.info("[joinTeamForm] joinCheck={}", joinCheck);
        if (joinCheck) {
            log.info("[joinTeamForm] 이미 팀에 가입된 선수");
            String redirectURI = "/team/" + teamCode;
            AlertMessageBox message = new AlertMessageBox("이미 팀에 가입되어 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        TeamMemberFormDTO joinTeamMember = TeamMemberFormDTO.builder()
                .teamName(joinTeam.getTeamName())
                .teamCode(teamCode)
                .memberName(loginMember.getName())
                .teamMemberShip(TeamMemberShip.PLAYER.getDescription())
                .build();
        model.addAttribute("joinTeamMember", joinTeamMember);

        return "teamMember/joinTeamMemberForm";
    }

    /**
     * 팀 가입
     */
    @PostMapping("/joinTeam/{teamCode}")
    public String joinTeam(@PathVariable String teamCode,
                           @Validated @ModelAttribute("joinTeamMember") TeamMemberFormDTO joinTeamMember,
                           BindingResult bindingResult,
                           HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "teamMember/joinTeamMemberForm";
        }
        log.info("[joinTeam] joinTeamMemberDto={}", joinTeamMember);

        MemberDTO loginMember = controllerUtil.getLoginMember(request);

        ResponseDataDTO<TeamMemberDTO> response = teamMemberService.joinTeamMember(joinTeamMember, loginMember);

        if (response.isError()) {
            controllerUtil.convertBindingError(bindingResult, response.getBindingErrors());
            log.info("[joinTeam] bindingResult={}", bindingResult);
            return "teamMember/joinTeamMemberForm";
        }

        TeamMemberDTO teamMemberDTO = response.getData();
        log.info("[joinTeam] teamMember={}", teamMemberDTO);

        return "redirect:/teamMember/" + teamMemberDTO.getTeamMemberId();
    }

    /**
     * 관리자의 팀원 수정 페이지
     */
    @GetMapping("{teamMemberId}/manage")
    public String manageTeamMemberForm(@PathVariable Long teamMemberId,
                                       Model model,
                                       HttpServletRequest request) {

        TeamMemberDTO teamMember = teamMemberService.findTeamMember(teamMemberId);
        MemberDTO loginMember = controllerUtil.getLoginMember(request);
        TeamMemberDTO loginTeamMember =
                teamMemberService.findTeamMember(loginMember.getMemberId(), teamMember.getTeam().getTeamCode());

        log.info("[manageTeamMemberForm] teamMember != loginTeamMember ={}", teamMember != loginTeamMember);

        Integer loginTeamMemberGrade = loginTeamMember.getMemberShip().getGrade();
        List<TeamMemberShip> teamMemberShips = new ArrayList<>();

        if (teamMember.getMemberShip().getGrade() == 1) {
            teamMemberShips.add(TeamMemberShip.OWNER);
        } else if (teamMember == loginTeamMember || loginTeamMemberGrade <= teamMember.getMemberShip().getGrade()) {
            teamMemberShips = getTeamMemberShips(loginTeamMemberGrade);
        } else {
            String redirectURI = "/team/" + teamMember.getTeam().getTeamCode();
            AlertMessageBox message = new AlertMessageBox("수정하려는 대상의 권한이 더 높습니다", redirectURI);
            model.addAttribute("message", message);
            log.info("[manageTeamMemberForm] AlertMessage redirectURI={}", redirectURI);
            return "template/alert";
        }
        log.info("[manageTeamMemberForm] teamMemberShips={}", teamMemberShips);

        TeamMemberFormDTO teamMemberForm = TeamMemberFormDTO.from(teamMember);

        model.addAttribute("teamMember", teamMember);
        model.addAttribute("teamMemberShips", teamMemberShips);
        model.addAttribute("teamMemberForm", teamMemberForm);

        return "teamMember/editTeamMemberForm";
    }

    /**
     * 관리자의 팀원 수정
     */
    @PostMapping("{teamMemberId}/manage")
    public String manageTeamMember(@PathVariable Long teamMemberId,
                                   @Validated @ModelAttribute("teamMemberForm") TeamMemberFormDTO teamMemberForm,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   Model model) {

        TeamMemberDTO teamMember = teamMemberService.findTeamMember(teamMemberId);
        MemberDTO loginMember = controllerUtil.getLoginMember(request);

        TeamMemberDTO loginTeamMember =
                teamMemberService.findTeamMember(loginMember.getMemberId(), teamMember.getTeam().getTeamCode());

        if (bindingResult.hasErrors()) {
            Integer loginMemberGrade = loginTeamMember.getMemberShip().getGrade();
            List<TeamMemberShip> teamMemberShips = getTeamMemberShips(loginMemberGrade);

            model.addAttribute("teamMember", teamMember);
            model.addAttribute("teamMemberShips", teamMemberShips);
            log.info("[manageTeamMember] has field error");
            return "teamMember/editTeamMemberForm";
        }

        TeamMemberShip editTeamMemberShip = TeamMemberShip.fromDescription(teamMemberForm.getTeamMemberShip());

        //수정하려는 등급이 감독 이상일 때
        //기존 감독이나 오너의 등급을 선수로 내린다
        if (teamMember.getMemberShip() != editTeamMemberShip && editTeamMemberShip.getGrade() <= TeamMemberShip.MANAGER.getGrade()) {
            TeamMemberDTO highTeamMember = teamMemberService.findTeamMembers(teamMember.getTeam().getTeamCode()).stream()
                    .filter(tm -> tm.getMemberShip().equals(editTeamMemberShip))
                    .findFirst()
                    .orElse(null);

            if (highTeamMember != null) {
                if (editTeamMemberShip.getGrade().equals(TeamMemberShip.OWNER.getGrade())) {
                    TeamDTO team = teamMember.getTeam();
                    MemberDTO member = teamMember.getMember();
                    teamService.changeOwner(team.getTeamCode(), member);
                    log.info("[manageTeamMember] change Owner={}", team.getOwner());
                }

                highTeamMember.setMemberShip(TeamMemberShip.PLAYER);
                teamMemberService.editTeamMember(highTeamMember);
                log.info("[manageTeamMember] change teamMemberShip={}", highTeamMember.getMember().getName());
            }
        }

        teamMember.setMemberShip(TeamMemberShip.fromDescription(teamMemberForm.getTeamMemberShip()));
        teamMember.setMainPosition(Position.fromDescription(teamMemberForm.getMainPosition()));
        teamMember.setBackNumber(teamMemberForm.getBackNumber());

        teamMemberService.editTeamMember(teamMember);
        log.info("[manageTeamMember] edit teamMember={}", teamMember);

        return "redirect:/teamMember/" + teamMemberId;
    }

    private static List<TeamMemberShip> getTeamMemberShips(Integer loginMemberGrade) {
        List<TeamMemberShip> teamMemberShips = new ArrayList<>();
        for (int i = 4; i >= loginMemberGrade; i--) {
            teamMemberShips.add(TeamMemberShip.fromGrade(i));
        }
        return teamMemberShips;
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return List.of(Position.values());
    }
}
