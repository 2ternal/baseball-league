package eternal.baseball.domain;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    private Long teamMemberId;
    private Member member;
    private Team team;
    private TeamMemberShip memberShip;
    private Position mainPosition;
    private Long backNumber;

    public TeamMember(Member member, Team team, TeamMemberShip teamMemberShip,
                      Position mainPosition, Long backNumber) {
        this.member = member;
        this.team = team;
        this.memberShip = teamMemberShip;
        this.mainPosition = mainPosition;
        this.backNumber = backNumber;
    }
}
