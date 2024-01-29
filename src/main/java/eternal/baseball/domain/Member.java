package eternal.baseball.domain;

import eternal.baseball.domain.custom.Birthday;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Member {

    private Long memberId;
    private String loginId;
    private String password;
    private String name;
    private Birthday birthday;

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Builder
    public Member(Long memberId, String loginId, String password, String name, Birthday birthday) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
    }
}
