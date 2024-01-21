package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinTeamMemberDto {

    private String teamName;
    private String memberName;
    @NotEmpty
    private String mainPosition;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;

    public JoinTeamMemberDto(Member member, Team team) {
        this.teamName = team.getTeamName();
        this.memberName = member.getName();
    }
}
