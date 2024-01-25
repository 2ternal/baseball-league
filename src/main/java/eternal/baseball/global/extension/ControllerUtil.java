package eternal.baseball.global.extension;

import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.global.constant.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerUtil {

    public void convertBindingError(BindingResult bindingResult, List<BindingErrorDTO> bindingErrors) {
        for (BindingErrorDTO bindingError : bindingErrors) {
            if (bindingError.getErrorField().isEmpty()) {
                bindingResult.reject(bindingError.getErrorCode(),
                        bindingError.getErrorMessage());
            } else {
                bindingResult.rejectValue(bindingError.getErrorField(),
                        bindingError.getErrorCode(),
                        bindingError.getErrorMessage());
            }
        }
        log.info("[joinTeam] bindingResult={}", bindingResult);
    }

    public void setLoginMember(HttpServletRequest request, MemberDTO loginMember) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        session.setAttribute(SessionConst.LOGIN_CHECK, SessionConst.LOGIN_VALID);
    }

    public MemberDTO getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (MemberDTO) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
