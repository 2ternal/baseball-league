package eternal.baseball.domain;

import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.dto.member.SignUpMemberDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Member {

    private Long memberId;
    private String loginId;
    private String password;
    private String name;
    private Birthday birthday;

    public Member() {
    }

    public Member(Long memberId, String loginId, String password, String name, Birthday birthday) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
    }
}
