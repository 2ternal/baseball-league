package eternal.baseball.dto.team;

import eternal.baseball.domain.Team;
import eternal.baseball.dto.member.MemberDTO;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {

    private String teamName;
    private String teamCode;
    private MemberDTO owner;

    public TeamDTO(Team team) {
        this.teamName = team.getTeamName();
        this.teamCode = team.getTeamCode();
        this.owner = MemberDTO.builder()
                .memberId(team.getOwner().getMemberId())
                .loginId(team.getOwner().getLoginId())
                .name(team.getOwner().getName())
                .birthday(team.getOwner().getBirthday())
                .build();
    }
}
