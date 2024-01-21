package eternal.baseball.dto.member;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.custom.Birthday;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class EditMemberDTO {

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

    public EditMemberDTO() {
    }

    public EditMemberDTO(MemberDTO member) {
        this.memberId = member.getMemberId();
        this.name = member.getName();
        this.loginId = member.getLoginId();
        this.year = member.getBirthday().getYear();
        this.month = member.getBirthday().getMonth();
        this.day = member.getBirthday().getDay();
    }

    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .loginId(loginId)
                .password(changePassword)
                .name(name)
                .birthday(new Birthday(year, month, day))
                .build();
    }
}
