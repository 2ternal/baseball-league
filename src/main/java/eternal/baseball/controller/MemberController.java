package eternal.baseball.controller;

import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.domain.Member;
import eternal.baseball.service.MemberService;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.member.EditMemberForm;
import eternal.baseball.dto.member.JoinMemberForm;
import eternal.baseball.service.TeamMemberService;
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
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final TeamMemberService teamMemberService;

    /**
     * 회원 목록 페이지
     */
    @GetMapping("/members")
    public String members(Model model) {

        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "member/members";
    }

    /**
     * 회원 가입 페이지
     */
    @GetMapping("/joinMember")
    public String joinMemberForm(Model model) {

        model.addAttribute("joinMemberForm", new JoinMemberForm());

        return "member/joinMemberForm";
    }

    /**
     * 회원 가입
     */
    @PostMapping("/joinMember")
    public String joinMember(@Validated @ModelAttribute("joinMemberForm") JoinMemberForm joinMemberForm,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("[joinMember] bindingResult={}", bindingResult);
            return "member/joinMemberForm";
        }

        // ID 중복 검증
        if (memberService.duplicateIdCheck(joinMemberForm.getLoginId())) {
            log.info("  duplicateId={}", joinMemberForm.getLoginId());
            bindingResult.rejectValue("loginId", "duplicateId", "중복되는 ID 입니다");
        }

        // 이름 중복 검증
        if (memberService.duplicateNameCheck(joinMemberForm.getName())) {
            log.info("  duplicateName={}", joinMemberForm.getName());
            bindingResult.rejectValue("name", "duplicateName", "중복되는 회원 이름 입니다");
        }

        // 비밀번호 일치 검증
        if (!Objects.equals(joinMemberForm.getPassword(), joinMemberForm.getPasswordCheck())) {
            log.info("  mismatchPassword={}", joinMemberForm.getPassword());
            bindingResult.rejectValue("passwordCheck", "mismatchPassword", "비밀번호가 일치하지 않습니다");
        }

        if (bindingResult.hasErrors()) {
            log.info("[joinMember] bindingResult={}", bindingResult);
            return "member/joinMemberForm";
        }

        Member member = new Member(joinMemberForm);
        memberService.joinMember(member);

        log.info("[joinMember] joinMember={}", member);

        return "redirect:members";
    }

    /**
     * 회원 정보 페이지
     */
    @GetMapping("/{memberId}")
    public String member(@PathVariable Long memberId, Model model) {

        Member member = memberService.findByMemberId(memberId);
        List<TeamMember> teamMemberList = teamMemberService.findByMemberId(member.getMemberId());

        model.addAttribute("member", member);
        model.addAttribute("teamMemberList", teamMemberList);

        return "member/member";
    }

    /**
     * 내 정보 페이지
     */
    @GetMapping("/myPage")
    public String myPageForm(Model model, HttpServletRequest request) {

        Member loginMember = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        List<TeamMember> teamMemberList = teamMemberService.findByMemberId(loginMember.getMemberId());

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("teamMemberList", teamMemberList);

        return "member/myPage";
    }

    /**
     * 회원 정보 수정 페이지
     */
    @GetMapping("/editMember")
    public String editMemberForm(@ModelAttribute("loginMember") EditMemberForm loginMember,
                                 HttpServletRequest request,
                                 Model model) {

        if (loginMember.getName() == null) {
            Member member = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
            loginMember = new EditMemberForm(member);
            model.addAttribute("loginMember", loginMember);
        }

        return "member/editMemberForm";
    }

    /**
     * 회원 정보 수정
     */
    @PostMapping("/editMember")
    public String editMember(@Validated @ModelAttribute("loginMember") EditMemberForm loginMember,
                             BindingResult bindingResult,
                             HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("[editMember] bindingResult={}", bindingResult);
            return "member/editMemberForm";
        }

        Member sessionMember = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);

        // 비밀번호 확인 검증
        if (!sessionMember.getPassword().equals(loginMember.getPassword())) {
            bindingResult.rejectValue("password", "wrongPassword", "비밀번호가 틀립니다");
            return "member/editMemberForm";
        }

        // 이름 중복 검증
        if (!Objects.equals(sessionMember.getName(), loginMember.getName()) && memberService.duplicateNameCheck(loginMember.getName())) {
            log.info("  duplicateName={}", sessionMember.getName());
            bindingResult.rejectValue("name", "duplicateName", "중복되는 회원 이름 입니다");
        }

        // 변경 비밀번호 일치 검증
        if (!loginMember.getChangePassword().equals(loginMember.getChangePasswordCheck())) {
            bindingResult.rejectValue("changePasswordCheck", "wrongChangePassword", "변경할 비밀번호가 일치하지 않습니다");
            return "member/editMemberForm";
        }

        if (bindingResult.hasErrors()) {
            log.info("[editMember] bindingResult={}", bindingResult);
            return "member/editMemberForm";
        }

        sessionMember.setName(loginMember.getName());
        sessionMember.setBirthday(new Birthday(loginMember.getYear(), loginMember.getMonth(), loginMember.getDay()));
        if (!loginMember.getChangePassword().isEmpty()) {
            sessionMember.setPassword(loginMember.getChangePassword());
        }

        Member editMember = memberService.editMember(loginMember.getMemberId(), sessionMember);

        log.info("[editMember] editMember={}", editMember);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, sessionMember);

        return "redirect:myPage";
    }
}
