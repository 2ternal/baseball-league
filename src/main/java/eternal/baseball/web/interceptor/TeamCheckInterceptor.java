package eternal.baseball.web.interceptor;

import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import eternal.baseball.web.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamCheckInterceptor implements HandlerInterceptor {

    private final TeamMemberRepository teamMemberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String referer = request.getHeader("REFERER");

        log.info("[TeamCheckInterceptor] 팀 가입 체크 인터셉터 실행 in [{}]", requestURI);
        log.info("[TeamCheckInterceptor] 이전 url={}", referer);

        Member loginMember = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        Long teamId = Long.parseLong(requestURI.substring(8, requestURI.lastIndexOf("/")));

        log.info("[TeamCheckInterceptor] 팀 Id={}", teamId);

        Boolean teamMemberCheck = teamMemberRepository.teamMemberCheck(loginMember.getMemberId(), teamId);

        if (teamMemberCheck) {
            response.sendRedirect("/alert/notInclude?redirectURL=" + referer);

            log.info("[TeamCheckInterceptor] 팀 멤버가 아닙니다");
            return false;
        }

        return true;
    }
}
