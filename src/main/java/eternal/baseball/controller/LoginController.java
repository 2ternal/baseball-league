package eternal.baseball.controller;

import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.global.extension.ControllerUtil;
import eternal.baseball.service.LoginService;
import eternal.baseball.dto.login.LoginDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final ControllerUtil controllerUtil;

    /**
     * 로그인 페이지
     */
    @GetMapping("login")
    public String loginForm(@ModelAttribute("loginRequest") LoginRequestDTO loginRequest) {
        return "login/loginForm";
    }

    /**
     * 로그인 요청
     */
    @PostMapping("login")
    public String login(@Validated @ModelAttribute("loginRequest") LoginRequestDTO loginRequest,
                        BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        ResponseDataDTO<MemberDTO> response = loginService.login(loginRequest);

        if (response.isError()) {
            controllerUtil.convertBindingError(bindingResult, response.getBindingErrors());
            log.info("[joinMember] bindingResult={}", bindingResult);
            return "login/loginForm";
        }

        MemberDTO loginMember = response.getData();
        log.info("[joinMember] loginMember={}", loginMember);

        controllerUtil.setLoginMember(request, loginMember);
        log.info("[login] login!");
        log.info("[login] redirectURL={}", redirectURL);

        if (redirectURL.equals("/")) {
            redirectURL = "member/members";
        }

        return "redirect:" + redirectURL;
    }

    /**
     * 로그아웃 요청
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
