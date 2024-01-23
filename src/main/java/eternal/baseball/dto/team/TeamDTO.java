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

    public static TeamDTO from(Team team) {
        return TeamDTO.builder()
                .teamName(team.getTeamName())
                .teamCode(team.getTeamCode())
                .owner(MemberDTO.from(team.getOwner()))
                .build();
    }
}
