package eternal.baseball.dto.team;

import eternal.baseball.dto.member.MemberDTO;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditTeamDTO {

    @NotEmpty
    private String teamName;
    @Size(min = 2, max = 5)
    @NotEmpty
    private String teamCode;
    private MemberDTO owner;
}
