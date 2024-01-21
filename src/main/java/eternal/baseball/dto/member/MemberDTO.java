package eternal.baseball.dto.member;

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
}
