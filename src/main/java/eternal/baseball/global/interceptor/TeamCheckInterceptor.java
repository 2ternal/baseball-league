package eternal.baseball.global.interceptor;

import eternal.baseball.domain.Member;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.global.constant.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamCheckInterceptor implements HandlerInterceptor {

    private final TeamMemberService teamMemberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String referer = request.getHeader("REFERER");

        log.info("[TeamCheckInterceptor] 팀 가입 체크 인터셉터 실행 in [{}]", requestURI);
        log.info("[TeamCheckInterceptor] 이전 url={}", referer);

        Member loginMember = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        int i = requestURI.indexOf("lineup/") + "lineup/".length();
        String substring = requestURI.substring(i);

        int j = substring.indexOf("/");
        String stringTeamId = substring.substring(0, j);
        log.info("[TeamCheckInterceptor] stringTeamId={}", stringTeamId);

        Long teamId = Long.parseLong(stringTeamId);

        log.info("[TeamCheckInterceptor] 팀 Id={}", teamId);

        Boolean teamMemberCheck = teamMemberService.joinTeamMemberCheck(loginMember.getMemberId(), teamId);

        if (!teamMemberCheck) {
            response.sendRedirect("/alert/notInclude?redirectURL=" + referer);

            log.info("[TeamCheckInterceptor] 팀 멤버가 아닙니다");
            return false;
        }

        return true;
    }
}
