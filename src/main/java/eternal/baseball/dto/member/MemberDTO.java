package eternal.baseball.dto.member;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.custom.Birthday;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDTO {

    private Long memberId;
    private String loginId;
    private String name;
    private Birthday birthday;

    public static MemberDTO from(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthday(member.getBirthday())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .loginId(loginId)
                .name(name)
                .birthday(birthday)
                .build();
    }
}
