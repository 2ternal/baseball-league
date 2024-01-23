package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberFormDTO {

    private String teamName;
    private String teamCode;
    private String memberName;
    @NotEmpty
    private String mainPosition;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;
    @NotEmpty
    private String teamMemberShip;

    public static TeamMemberFormDTO from(TeamMemberDTO teamMemberDTO) {
        return TeamMemberFormDTO.builder()
                .teamName(teamMemberDTO.getTeam().getTeamName())
                .memberName(teamMemberDTO.getMember().getName())
                .mainPosition(teamMemberDTO.getMainPosition().getDescription())
                .backNumber(teamMemberDTO.getBackNumber())
                .teamMemberShip(teamMemberDTO.getMemberShip().getDescription())
                .build();
    }

    public TeamMember toEntity(Member member, Team team) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .memberShip(TeamMemberShip.fromDescription(teamMemberShip))
                .mainPosition(Position.fromDescription(mainPosition))
                .backNumber(backNumber)
                .build();
    }
}
