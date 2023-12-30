package eternal.baseball.web.member;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class EditMemberForm {

    private Long memberId;
    @NotEmpty
    private String name;
    private String loginId;
    private String password;
    private String changePassword;
    private String changePasswordCheck;
    @NotNull
    private Integer year;
    @Min(value = 1)
    @Max(value = 12)
    @NotNull
    private Integer month;
    @Min(value = 1)
    @Max(value = 31)
    @NotNull
    private Integer day;

    public EditMemberForm() {
    }
}
