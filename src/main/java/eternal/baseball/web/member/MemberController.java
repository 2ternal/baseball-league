package eternal.baseball.web.member;

import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.member.MemberRepository;
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

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members")
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "member/members";
    }

    @GetMapping("/joinMember")
    public String joinMemberForm(Model model) {
        model.addAttribute("joinMemberForm", new JoinMemberForm());
        return "member/joinMemberForm";
    }

    @PostMapping("/joinMember")
    public String joinMember(@Validated @ModelAttribute("joinMemberForm") JoinMemberForm joinMemberForm,
                             BindingResult bindingResult) {

        // ID 중복 검증
        if (!ObjectUtils.isEmpty(memberRepository.findByLoginId(joinMemberForm.getLoginId()))) {
            log.info("  duplicateId={}", joinMemberForm.getLoginId());
            bindingResult.reject("duplicateId", "중복되는 ID 입니다");
            log.info("bindingResult={}", bindingResult);
        }

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "member/joinMemberForm";
        }

        Member member = new Member();
        member.JoinMemberFormToMember(joinMemberForm);
        log.info("joinMember={}", member);

        memberRepository.save(member);
        return "redirect:members";
    }
}
