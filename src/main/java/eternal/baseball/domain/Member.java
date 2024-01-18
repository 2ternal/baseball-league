package eternal.baseball.domain;

import eternal.baseball.domain.custom.Birthday;
import eternal.baseball.dto.member.JoinMemberForm;
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

    public Member(JoinMemberForm joinMemberForm) {
        Birthday joinMemberBirthday = new Birthday(joinMemberForm.getBirthdayYYYY(),
                joinMemberForm.getBirthdayMM(),
                joinMemberForm.getBirthdayDD());
        this.setLoginId(joinMemberForm.getLoginId());
        this.setPassword(joinMemberForm.getPassword());
        this.setName(joinMemberForm.getName());
        this.setBirthday(joinMemberBirthday);
    }
}
