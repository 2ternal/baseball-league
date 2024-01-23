package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.TeamMember;
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
public class EditTeamMemberDto {

    private String teamName;
    private String memberName;
    @NotEmpty
    private String mainPosition;
    @Min(value = 0)
    @Max(value = 130)
    @NotNull
    private Long backNumber;
    @NotEmpty
    private String teamMemberShip;

    public static EditTeamMemberDto from(TeamMemberDTO teamMemberDTO) {
        return EditTeamMemberDto.builder()
                .teamName(teamMemberDTO.getTeam().getTeamName())
                .memberName(teamMemberDTO.getMember().getName())
                .mainPosition(teamMemberDTO.getMainPosition().getDescription())
                .backNumber(teamMemberDTO.getBackNumber())
                .teamMemberShip(teamMemberDTO.getMemberShip().getDescription())
                .build();
    }
}
