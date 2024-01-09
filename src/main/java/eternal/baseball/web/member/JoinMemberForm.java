package eternal.baseball.web.member;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class JoinMemberForm {

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordCheck;
    @NotEmpty
    private String name;

    @NotNull
    private Integer birthdayYYYY;
    @Min(value = 1)
    @Max(value = 12)
    @NotNull
    private Integer birthdayMM;
    @Min(value = 1)
    @Max(value = 31)
    @NotNull
    private Integer birthdayDD;
}
