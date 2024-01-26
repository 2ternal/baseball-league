package eternal.baseball.controller;

import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.lineup.LineupChangeDTO;
import eternal.baseball.dto.lineup.LineupDTO;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.global.extension.ControllerUtil;
import eternal.baseball.service.LineupService;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import eternal.baseball.global.extension.AlertMessageBox;
import eternal.baseball.dto.lineup.LineupChangeCard;
import eternal.baseball.dto.lineup.LineupFormDTO;
import eternal.baseball.global.constant.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lineup")
public class LineupController {

    private final TeamMemberService teamMemberService;
    private final LineupService lineupService;
    private final ControllerUtil controllerUtil;

    /**
     * 라인업 상세 페이지
     */
    @GetMapping("/{teamCode}/{lineupId}")
    public String lineup(@PathVariable String teamCode, @PathVariable Long lineupId, Model model) {
        LineupDTO lineup = lineupService.findLineup(lineupId);
        model.addAttribute("lineup", lineup);
        return "lineup/lineup";
    }

    /**
     * 라인업 목록 페이지
     */
    @GetMapping("/{teamCode}/list")
    public String lineupList(@PathVariable String teamCode, Model model) {
        List<LineupDTO> teamLineupList = lineupService.findTeamLineupList(teamCode);
        model.addAttribute("lineupList", teamLineupList);
        model.addAttribute("teamCode", teamCode);
        return "lineup/lineupList";
    }

    /**
     * 라인업 작성 페이지
     */
    @GetMapping("/{teamCode}/create")
    public String writeLineupForm(@PathVariable String teamCode,
                                  @ModelAttribute("lineupForm") LineupFormDTO lineupForm,
                                  @ModelAttribute("errCode") String errCode,
                                  @ModelAttribute("errMessage") String errMessage,
                                  @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                  BindingResult bindingResult,
                                  HttpServletRequest request,
                                  Model model) {

        log.info("[writeLineupForm] init lineupForm={}", lineupForm);
        log.info("[writeLineupForm] init errCode={}", errCode);
        log.info("[writeLineupForm] init errCode isEmpty={}", errCode.isEmpty());
        log.info("[writeLineupForm] init errMessage={}", errMessage);

        if (!errCode.isEmpty()) {
            bindingResult.reject(errCode, errMessage);
            log.info("[writeLineupForm] bindingResult={}", bindingResult);
            return "lineup/writeLineupForm";
        }

        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        TeamMemberDTO loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        if (loginTeamMember.getMemberShip().getGrade() > 3) {
            AlertMessageBox message = controllerUtil.makeAlertMessage(request, "라인업은 코치등급부터 작성할 수 있습니다");
            model.addAttribute("message", message);
            return "template/alert";
        }

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMemberDTO> teamMembers = teamMemberService.findTeamMembers(teamCode);
            if (teamMembers.size() < 9) {
                log.info("[writeLineupForm] be short of teamMember={}", teamMembers.size());
                AlertMessageBox message = controllerUtil.makeAlertMessage(request, "팀원이 부족합니다");
                model.addAttribute("message", message);
                return "template/alert";
            }

            lineupForm = LineupFormDTO.fromTeamMembers(teamMembers);
        }

        lineupChangeCard = LineupChangeCard.builder()
                .lineupName(lineupForm.getLineupName())
                .build();

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[writeLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", lineupChangeCard);
        model.addAttribute("teamCode", teamCode);

        //Post 메소드 실행에 쓰기 위해 session 에 담는다
        controllerUtil.setLineup(request, lineupForm);
        return "lineup/writeLineupForm";
    }

    /**
     * 라인업 작성 페이지 타순 교체
     */
    @PostMapping("/{teamCode}/changeOrder")
    public String changeOrder(@PathVariable String teamCode,
                              @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {

        String redirectURI = request.getHeader(SessionConst.PREVIOUS_URI);
        log.info("[changeOrder] REFERER redirectURI={}", redirectURI);

        if (bindingResult.hasErrors()) {
            return "lineup/writeLineupForm";
        }

        LineupFormDTO lineupForm = controllerUtil.getLineupForm(request);

        LineupChangeDTO responseData = lineupService.changeOrder(lineupChangeCard, lineupForm).getData();

        if (!responseData.getStatus()) {
            log.info("[changeOrder] ====player1 is null====");
            redirectAttributes.addFlashAttribute("lineupForm", responseData.getLineupForm());
            redirectAttributes.addFlashAttribute("errCode", responseData.getErrCode());
            redirectAttributes.addFlashAttribute("errMessage", responseData.getErrMessage());
            redirectAttributes.addFlashAttribute("lineupChangeCard", lineupChangeCard);

            return "redirect:" + redirectURI;
        }

        //redirect 설정
        //세션에 담지 않고 redirectAttributes 로 플래시 세션에 담는다. 리다리엑트 후 소멸한다
        redirectAttributes.addFlashAttribute("lineupForm", responseData.getLineupForm());
        redirectAttributes.addFlashAttribute("status", "true");

        return "redirect:" + redirectURI;
    }

    /**
     * 라인업 작성 페이지 포지션 교체
     */
    @PostMapping("/{teamCode}/changePosition")
    public String changePosition(@PathVariable String teamCode,
                                 @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        String redirectURI = request.getHeader(SessionConst.PREVIOUS_URI);
        log.info("[changePosition] REFERER redirectURI={}", redirectURI);

        if (bindingResult.hasErrors()) {
            return "lineup/writeLineupForm";
        }

        LineupFormDTO lineupForm = controllerUtil.getLineupForm(request);

        LineupChangeDTO responseData = lineupService.changePosition(lineupChangeCard, lineupForm).getData();

        if (!responseData.getStatus()) {
            redirectAttributes.addFlashAttribute("lineupForm", responseData.getLineupForm());
            redirectAttributes.addFlashAttribute("errCode", responseData.getErrCode());
            redirectAttributes.addFlashAttribute("errMessage", responseData.getErrMessage());
            redirectAttributes.addFlashAttribute("lineupChangeCard", lineupChangeCard);

            return "redirect:" + redirectURI;
        }

        //redirect 설정
        redirectAttributes.addFlashAttribute("lineupForm", responseData.getLineupForm());
        redirectAttributes.addFlashAttribute("status", "true");

        return "redirect:" + redirectURI;
    }

    /**
     * 라인업 작성
     */
    @PostMapping("/{teamCode}/create")
    public String writeLineup(@PathVariable String teamCode,
                              @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              Model model) {

        LineupFormDTO lineupForm = controllerUtil.getLineupForm(request);
        log.info("[writeLineup] lineupChangeCard={}", lineupChangeCard);
        log.info("[writeLineup] lineupForm={}", lineupForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamCode", teamCode);
            log.info("[writeLineup] bindingResult={}", bindingResult);
            return "lineup/writeLineupForm";
        }

        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        lineupForm.setLineupName(lineupChangeCard.getLineupName());
        lineupForm.setTeamCode(teamCode);

        LineupDTO lineupDTO = lineupService.createLineup(lineupForm, loginMemberDTO);
        log.info("[writeLineup] lineup={}", lineupDTO);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    /**
     * 라인업 수정 폼
     */
    @GetMapping("/{teamCode}/{lineupId}/edit")
    public String editLineupForm(@PathVariable String teamCode,
                                 @PathVariable Long lineupId,
                                 @ModelAttribute("lineupForm") LineupFormDTO lineupForm,
                                 @ModelAttribute("errCode") String errCode,
                                 @ModelAttribute("errMessage") String errMessage,
                                 @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 Model model) {

        log.info("[editLineupForm] init lineupForm={}", lineupForm);
        log.info("[editLineupForm] init errCode={}", errCode);
        log.info("[editLineupForm] init errCode isEmpty={}", errCode.isEmpty());
        log.info("[editLineupForm] init errMessage={}", errMessage);

        if (!errCode.isEmpty()) {
            bindingResult.reject(errCode, errMessage);
            log.info("[editLineupForm] bindingResult={}", bindingResult);
            return "lineup/editLineupForm";
        }

        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        TeamMemberDTO loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        if (loginTeamMember.getMemberShip().getGrade() > 3) {
            AlertMessageBox message = controllerUtil.makeAlertMessage(request, "라인업은 코치등급부터 작성할 수 있습니다");
            model.addAttribute("message", message);
            return "template/alert";
        }

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMemberDTO> teamMembers = teamMemberService.findTeamMembers(teamCode);
            if (teamMembers.size() < 9) {
                log.info("[editLineupForm] be short of teamMember={}", teamMembers.size());
                AlertMessageBox message = controllerUtil.makeAlertMessage(request, "팀원이 부족합니다");
                model.addAttribute("message", message);
                return "template/alert";
            }

            LineupDTO lineup = lineupService.findLineup(lineupId);

            lineupForm = LineupFormDTO.from(lineup);
            log.info("[editLineupForm] new lineupForm");
        }

        lineupChangeCard = LineupChangeCard.builder()
                .lineupName(lineupForm.getLineupName())
                .build();

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[editLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupId", lineupId);
        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", lineupChangeCard);
        model.addAttribute("teamCode", teamCode);

        //Post 메소드 실행에 쓰기 위해 session 에 담는다
        controllerUtil.setLineup(request, lineupForm);

        return "lineup/editLineupForm";
    }

    /**
     * 라인업 수정
     */
    @PostMapping("/{teamCode}/{lineupId}/edit")
    public String editLineup(@PathVariable String teamCode,
                             @PathVariable Long lineupId,
                             @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             Model model) {

        LineupFormDTO lineupForm = controllerUtil.getLineupForm(request);
        log.info("[editLineup] lineupForm={}", lineupForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupId", lineupId);
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamCode", teamCode);
            log.info("[editLineup] bindingResult={}", bindingResult);
            return "lineup/editLineupForm";
        }

        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        lineupForm.setLineupName(lineupChangeCard.getLineupName());

        LineupDTO lineupDTO = lineupService.editLineup(lineupId, lineupForm, loginMemberDTO);
        log.info("[editLineup] lineup={}", lineupDTO);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    /**
     * 라인업 다른 이름으로 저장
     */
    @PostMapping("/{teamCode}/{lineupId}/saveAs")
    public String saveAsDifferentLineup(@PathVariable String teamCode,
                                        @PathVariable Long lineupId,
                                        @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                        BindingResult bindingResult,
                                        HttpServletRequest request,
                                        Model model) {

        LineupFormDTO lineupForm = controllerUtil.getLineupForm(request);
        log.info("[saveAsDifferentLineup] lineupForm={}", lineupForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupId", lineupId);
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamCode", teamCode);
            log.info("[saveAsDifferentLineup] bindingResult={}", bindingResult);
            return "lineup/editLineupForm";
        }

        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        lineupForm.setLineupName(lineupChangeCard.getLineupName());

        LineupDTO lineupDTO = lineupService.createLineup(lineupForm, loginMemberDTO);
        log.info("[saveAsDifferentLineup] lineup={}", lineupDTO);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    /**
     * 라인업 삭제
     */
    @PostMapping("/{teamCode}/{lineupId}/delete")
    public String deleteLineup(@PathVariable String teamCode,
                               @PathVariable Long lineupId,
                               HttpServletRequest request,
                               Model model) {

        MemberDTO loginMemberDTO = controllerUtil.getLoginMember(request);
        TeamMemberDTO loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);
        LineupDTO lineup = lineupService.findLineup(lineupId);

        if (!loginTeamMember.getMemberShip().equals(TeamMemberShip.OWNER) && loginTeamMember != lineup.getWriter()) {
            AlertMessageBox message = controllerUtil.makeAlertMessage(request, "이 라인업을 삭제할 권한이 없습니다");
            model.addAttribute("message", message);
            return "template/alert";
        }

        log.info("[deleteLineup] deleteLineup!!");
        lineupService.deleteLineup(lineupId);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    @ModelAttribute("lineupNumber")
    public List<String> lineupNumber() {
        List<String> lineupNumber = new ArrayList<>();
        lineupNumber.add("one");
        lineupNumber.add("two");
        lineupNumber.add("three");
        lineupNumber.add("four");
        lineupNumber.add("five");
        lineupNumber.add("six");
        lineupNumber.add("seven");
        lineupNumber.add("eight");
        lineupNumber.add("nine");
        lineupNumber.add("bench");
        return lineupNumber;
    }
}
