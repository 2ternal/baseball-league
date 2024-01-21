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

    public EditMemberDTO(eternal.baseball.dto.member.MemberDTO memberDTO) {
        this.memberId = memberDTO.getMemberId();
        this.name = memberDTO.getName();
        this.loginId = memberDTO.getLoginId();
        this.year = memberDTO.getBirthday().getYear();
        this.month = memberDTO.getBirthday().getMonth();
        this.day = memberDTO.getBirthday().getDay();
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
