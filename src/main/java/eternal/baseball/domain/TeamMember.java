package eternal.baseball.domain;

import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    private Long teamMemberId;
    private Member member;
    private Team team;
    private TeamMemberShip memberShip;
    private Position mainPosition;
    private Long backNumber;

    public void setTeamMemberId(Long teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    @Builder
    public TeamMember(Long teamMemberId, Member member, Team team,
                      TeamMemberShip memberShip, Position mainPosition, Long backNumber) {
        this.teamMemberId = teamMemberId;
        this.member = member;
        this.team = team;
        this.memberShip = memberShip;
        this.mainPosition = mainPosition;
        this.backNumber = backNumber;
    }
}
