package eternal.baseball;

import eternal.baseball.global.interceptor.LogInterceptor;
import eternal.baseball.global.interceptor.LoginCheckInterceptor;
import eternal.baseball.global.interceptor.TeamCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TeamCheckInterceptor teamCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/css/**", "/*.ico", "/error",
                        "/member/**", "/team/teams",
                        "/login", "/logout");

        registry.addInterceptor(teamCheckInterceptor)
                .order(3)
                .addPathPatterns("/lineup/**")
                .excludePathPatterns("/", "/css/**", "/*.ico", "/error");
    }
}
