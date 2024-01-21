package eternal.baseball.dto.login;

import lombok.*;

import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@Builder
public class LoginDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequestDTO {
        @NotEmpty
        private String loginId;
        @NotEmpty
        private String password;
    }
}
