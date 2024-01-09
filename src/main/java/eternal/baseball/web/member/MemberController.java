package eternal.baseball.web.member;

import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.member.MemberRepository;
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

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 회원 목록 페이지
     */
    @GetMapping("/members")
    public String members(Model model) {

        List<Member> members = memberRepository.findAll();
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

        // ID 중복 검증
        if (!ObjectUtils.isEmpty(memberRepository.findByLoginId(joinMemberForm.getLoginId()))) {
            log.info("  duplicateId={}", joinMemberForm.getLoginId());
            bindingResult.rejectValue("loginId", "duplicateId", "중복되는 ID 입니다");
        }

        // 이름 중복 검증
        if (!ObjectUtils.isEmpty(memberRepository.findByName(joinMemberForm.getName()))) {
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

        Member member = new Member();
        member.JoinMemberFormToMember(joinMemberForm);
        log.info("[joinMember] joinMember={}", member);

        memberRepository.save(member);
        return "redirect:members";
    }

    /**
     * 회원 정보 페이지
     */
    @GetMapping("/{memberId}")
    public String member(@PathVariable Long memberId, Model model) {

        Member member = memberRepository.findByMemberId(memberId);
        List<TeamMember> teamMemberList = teamMemberRepository.findByMemberName(member.getName());

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
        List<TeamMember> teamMemberList = teamMemberRepository.findByMemberName(loginMember.getName());

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("teamMemberList", teamMemberList);

        return "member/myPage";
    }

    /**
     * 회원 정보 수정 페이지
     */
    @GetMapping("/editMember")
    public String editMemberForm(@ModelAttribute("loginMember") EditMemberForm loginMember,
                                 HttpServletRequest request) {
        if (loginMember.getName() == null) {
            Member member = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
            loginMember.setMemberId(member.getMemberId());
            loginMember.setLoginId(member.getLoginId());
            loginMember.setName(member.getName());
            loginMember.setYear(member.getBirthday().getYear());
            loginMember.setMonth(member.getBirthday().getMonth());
            loginMember.setDay(member.getBirthday().getDay());
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

        if (!sessionMember.getPassword().equals(loginMember.getPassword())) {
            bindingResult.reject("wrongPassword", "비밀번호가 틀립니다");
            return "member/editMemberForm";
        }

        if (!loginMember.getChangePassword().equals(loginMember.getChangePasswordCheck())) {
            bindingResult.reject("wrongChangePassword", "변경할 비밀번호가 일치하지 않습니다");
            return "member/editMemberForm";
        }

        sessionMember.setName(loginMember.getName());
        sessionMember.setBirthday(new Birthday(loginMember.getYear(), loginMember.getMonth(), loginMember.getDay()));
        if (!loginMember.getChangePassword().isEmpty()) {
            sessionMember.setPassword(loginMember.getChangePassword());
        }

        Member editMember = memberRepository.edit(loginMember.getMemberId(), sessionMember);

        log.info("[editMember] editMember={}", editMember);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, sessionMember);

        return "redirect:myPage";
    }
}
