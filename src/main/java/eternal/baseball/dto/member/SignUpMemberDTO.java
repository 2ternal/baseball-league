package eternal.baseball.dto.member;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.custom.Birthday;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SignUpMemberDTO {

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

    public Member toEntity() {
        return Member.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .birthday(new Birthday(birthdayYYYY, birthdayMM, birthdayDD))
                .build();
    }
}
