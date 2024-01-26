package eternal.baseball.global.extension;

import eternal.baseball.dto.lineup.LineupFormDTO;
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

    public AlertMessageBox makeAlertMessage(HttpServletRequest request, String alertMessage) {
        String redirectURI = request.getHeader(SessionConst.PREVIOUS_URI);
        return AlertMessageBox.builder()
                .redirectURI(redirectURI)
                .message(alertMessage)
                .build();
    }

    public void setLoginMember(HttpServletRequest request, MemberDTO loginMember) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        session.setAttribute(SessionConst.LOGIN_CHECK, SessionConst.LOGIN_VALID);
    }

    public MemberDTO getLoginMember(HttpServletRequest request) {
        return (MemberDTO) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
    }

    public void setLineup(HttpServletRequest request, LineupFormDTO lineup) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LINEUP_CARD, lineup);
    }

    public LineupFormDTO getLineupForm(HttpServletRequest request) {
        return (LineupFormDTO) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
    }
}
