package eternal.domain;

import eternal.domain.custom.Birthday;
import lombok.Data;

@Data
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
