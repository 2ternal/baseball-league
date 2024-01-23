package eternal.baseball.controller;


import eternal.baseball.domain.Member;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.TeamDTO;
import eternal.baseball.dto.teamMember.RequestTeamMemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import eternal.baseball.global.extension.AlertMessage;
import eternal.baseball.global.constant.SessionConst;
import eternal.baseball.dto.teamMember.EditTeamMemberDto;
import eternal.baseball.dto.teamMember.JoinTeamMemberDto;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/teamMember")
public class TeamMemberController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    /**
     * 팀원 상세 페이지
     */
    @GetMapping("{teamMemberId}")
    public String teamMember(@PathVariable Long teamMemberId, Model model) {

        TeamMemberDTO teamMember = teamMemberService.findTeamMember2(teamMemberId);
        model.addAttribute("teamMember", teamMember);

        return "teamMember/teamMember";
    }

    /**
     * 팀 가입 페이지
     */
    @GetMapping("/joinTeam/{teamCode}")
    public String joinTeamForm(@PathVariable String teamCode, Model model, HttpServletRequest request) {

        MemberDTO loginMember = getLoginMember(request);
        TeamDTO joinTeam = teamService.findTeam(teamCode);

        Boolean joinCheck = teamMemberService.joinTeamMemberCheck(loginMember.getMemberId(), joinTeam.getTeamCode());
        log.info("[joinTeamForm] joinCheck={}", joinCheck);
        if (joinCheck) {
            log.info("[joinTeamForm] 이미 팀에 가입된 선수");
            String redirectURI = "/team/" + teamCode;
            AlertMessage message = new AlertMessage("이미 팀에 가입되어 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        JoinTeamMemberDto joinTeamMemberDto = JoinTeamMemberDto.builder()
                .teamName(joinTeam.getTeamName())
                .memberName(loginMember.getName())
                .build();
        model.addAttribute("joinTeamMemberDto", joinTeamMemberDto);

        return "teamMember/joinTeamMemberForm";
    }

    /**
     * 팀 가입
     */
    @PostMapping("/joinTeam/{teamCode}")
    public String joinTeam(@PathVariable String teamCode,
                           @Validated @ModelAttribute("joinTeamMemberDto") JoinTeamMemberDto joinTeamMemberDto,
                           BindingResult bindingResult,
                           HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "teamMember/joinTeamMemberForm";
        }

        log.info("[joinTeam] joinTeamMemberDto={}", joinTeamMemberDto);
        MemberDTO loginMember = getLoginMember(request);

        RequestTeamMemberDTO requestDTO = RequestTeamMemberDTO.builder()
                .teamCode(teamCode)
                .member(loginMember)
                .teamMemberShip(TeamMemberShip.PLAYER)
                .mainPositionEng(joinTeamMemberDto.getMainPosition())
                .backNumber(joinTeamMemberDto.getBackNumber())
                .build();

        ResponseDataDTO<TeamMemberDTO> responseDTO = teamMemberService.joinTeamMember(requestDTO);

        TeamMemberDTO teamMemberDTO = responseDTO.getData();
        Long teamMemberId = teamMemberDTO.getTeamMemberId();
        log.info("[joinTeam] teamMember={}", teamMemberDTO);

        return "redirect:/teamMember/" + teamMemberId;
    }

    /**
     * 관리자의 팀원 수정 페이지
     */
    @GetMapping("{teamMemberId}/manage")
    public String manageTeamMemberForm(@PathVariable Long teamMemberId,
                                       Model model,
                                       HttpServletRequest request) {

        TeamMemberDTO teamMember = teamMemberService.findTeamMember2(teamMemberId);
        MemberDTO loginMember = getLoginMember(request);

        TeamMemberDTO loginTeamMember =
                teamMemberService.findTeamMember2(loginMember.getMemberId(), teamMember.getTeam().getTeamCode());

        Integer loginTeamMemberGrade = loginTeamMember.getMemberShip().getGrade();

        log.info("[manageTeamMemberForm] teamMember != loginTeamMember ={}", teamMember != loginTeamMember);

        if (teamMember != loginTeamMember && loginTeamMemberGrade > teamMember.getMemberShip().getGrade()) {
            String redirectURI = "/team/" + teamMember.getTeam().getTeamCode();
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

        EditTeamMemberDto editTeamMemberDto = EditTeamMemberDto.from(teamMember);

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

        TeamMemberDTO teamMember = teamMemberService.findTeamMember2(teamMemberId);
        MemberDTO loginMember = getLoginMember(request);

        TeamMember loginTeamMember =
                teamMemberService.findTeamMember(loginMember.getMemberId(), teamMember.getTeam().getTeamCode());

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
            TeamMemberDTO highTeamMember = teamMemberService.findTeamMembers2(teamMember.getTeam().getTeamCode()).stream()
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

        teamMember.setMemberShip(TeamMemberShip.fromDescription(editTeamMemberDto.getTeamMemberShip()));
        teamMember.setMainPosition(Position.fromDescription(editTeamMemberDto.getMainPosition()));
        teamMember.setBackNumber(editTeamMemberDto.getBackNumber());

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

    private static MemberDTO getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (MemberDTO) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return List.of(Position.values());
    }
}
