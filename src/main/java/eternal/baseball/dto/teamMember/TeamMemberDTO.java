package eternal.baseball.dto.teamMember;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.Team;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.team.TeamDTO;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDTO {

    private Long teamMemberId;
    private MemberDTO member;
    private TeamDTO team;
    private TeamMemberShip memberShip;
    private Position mainPosition;
    private Long backNumber;

    public static TeamMemberDTO from(TeamMember teamMember) {
        if (teamMember == null) {
            return null;
        }
        return TeamMemberDTO.builder()
                .teamMemberId(teamMember.getTeamMemberId())
                .member(MemberDTO.from(teamMember.getMember()))
                .team(TeamDTO.from(teamMember.getTeam()))
                .memberShip(teamMember.getMemberShip())
                .mainPosition(teamMember.getMainPosition())
                .backNumber(teamMember.getBackNumber())
                .build();
    }

    public TeamMember toEntity(Team team, Member member) {
        return TeamMember.builder()
                .teamMemberId(teamMemberId)
                .member(member)
                .team(team)
                .memberShip(memberShip)
                .mainPosition(mainPosition)
                .backNumber(backNumber)
                .build();
    }
}
