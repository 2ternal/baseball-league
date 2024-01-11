package eternal.baseball.domain.custom;

import java.util.stream.Stream;

public enum TeamMemberShip {

    OWNER(1, "구단주"), MANAGER(2, "감독"), COACH(3, "코치"), PLAYER(4, "선수");

    public final Integer grade;
    public final String description;

    TeamMemberShip(Integer grade, String description) {
        this.grade = grade;
        this.description = description;
    }

    public static TeamMemberShip fromGrade(Integer grade) {
        return Stream.of(TeamMemberShip.values())
                .filter(tm -> tm.getGrade().equals(grade))
                .findFirst()
                .orElse(null);
    }

    public static TeamMemberShip fromDescription(String description) {
        return Stream.of(TeamMemberShip.values())
                .filter(tm -> tm.getDescription().equals(description))
                .findFirst()
                .orElse(null);
    }

    public Integer getGrade() {
        return grade;
    }

    public String getDescription() {
        return description;
    }
}
