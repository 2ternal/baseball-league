package eternal.baseball.controller;

import eternal.baseball.dto.member.*;
import eternal.baseball.dto.teamMember.TeamMemberDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.global.extension.ControllerUtil;
import eternal.baseball.service.MemberService;
import eternal.baseball.service.TeamMemberService;
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
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final TeamMemberService teamMemberService;
    private final ControllerUtil controllerUtil;

    /**
     * 회원 목록 페이지
     */
    @GetMapping("/members")
    public String members(Model model) {

        List<MemberDTO> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "member/members";
    }

    /**
     * 회원 가입 페이지
     */
    @GetMapping("/signUpMember")
    public String signUpMemberForm(Model model) {

        model.addAttribute("signUpMember", new SignUpMemberDTO());

        return "member/signUpMemberForm";
    }

    /**
     * 회원 가입
     */
    @PostMapping("/signUpMember")
    public String signUpMember(@Validated @ModelAttribute("signUpMember") SignUpMemberDTO signUpMember,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("[signUpMember] bindingResult={}", bindingResult);
            return "member/signUpMemberForm";
        }

        ResponseDataDTO<MemberDTO> response = memberService.signUp(signUpMember);

        if (response.isError()) {
            controllerUtil.convertBindingError(bindingResult, response.getBindingErrors());
            log.info("[signUpMember] bindingResult={}", bindingResult);
            return "member/signUpMemberForm";
        }

        log.info("[signUpMember] signUpMember={}", signUpMember);

        return "redirect:members";
    }

    /**
     * 회원 정보 페이지
     */
    @GetMapping("/{memberId}")
    public String member(@PathVariable Long memberId, Model model) {

        MemberDTO member = memberService.findMember(memberId);
        List<TeamMemberDTO> teamMemberList = teamMemberService.findByMemberId(member.getMemberId());

        model.addAttribute("member", member);
        model.addAttribute("teamMemberList", teamMemberList);

        return "member/member";
    }

    /**
     * 내 정보 페이지
     */
    @GetMapping("/myPage")
    public String myPageForm(Model model, HttpServletRequest request) {

        MemberDTO loginMember = controllerUtil.getLoginMember(request);
        List<TeamMemberDTO> teamMemberList = teamMemberService.findByMemberId(loginMember.getMemberId());

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("teamMemberList", teamMemberList);

        return "member/myPage";
    }

    /**
     * 회원 정보 수정 페이지
     */
    @GetMapping("/editMember")
    public String editMemberForm(@ModelAttribute("editMember") EditMemberDTO editMember,
                                 HttpServletRequest request,
                                 Model model) {

        if (editMember.getName() == null) {
            MemberDTO member = controllerUtil.getLoginMember(request);
            editMember = EditMemberDTO.fromDTO(member);
            model.addAttribute("editMember", editMember);
            log.info("[editMemberForm] editMember={}", editMember);
        }

        return "member/editMemberForm";
    }

    /**
     * 회원 정보 수정
     */
    @PostMapping("/editMember")
    public String editMember(@Validated @ModelAttribute("editMember") EditMemberDTO editMember,
                             BindingResult bindingResult,
                             HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("[editMember] bindingResult={}", bindingResult);
            return "member/editMemberForm";
        }

        MemberDTO sessionMember = controllerUtil.getLoginMember(request);

        ResponseDataDTO<MemberDTO> response = memberService.editMember(sessionMember, editMember);

        if (response.isError()) {
            controllerUtil.convertBindingError(bindingResult, response.getBindingErrors());
            log.info("[editMember] bindingResult={}", bindingResult);
            return "member/editMemberForm";
        }

        MemberDTO editSessionMemberDTO = response.getData();
        log.info("[editMember] editMember={}", editSessionMemberDTO);

        controllerUtil.setLoginMember(request, editSessionMemberDTO);

        return "redirect:myPage";
    }
}
