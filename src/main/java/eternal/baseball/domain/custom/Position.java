package eternal.baseball.domain.custom;

import java.util.stream.Stream;

public enum Position {

    FIRST("1루수", 3), SECOND("2루수", 4), THIRD("3루수", 5), SHORT("유격수", 6),
    LEFT("좌익수", 7), CENTER("중견수", 8), RIGHT("우익수", 9),
    CATCHER("포수", 2), PITCHER("투수", 1), DH("지명타자", 10), NONE("없음", 0);

    public final String description;
    public final Integer orderNum;

    Position(String description, Integer orderNum) {
        this.description = description;
        this.orderNum = orderNum;
    }

    public static Position from(String description) {
        return Stream.of(Position.values())
                .filter(p -> p.getDescription().equals(description))
                .findFirst()
                .orElse(null);
    }

    public static Position fromOrder(Integer orderNum) {
        return Stream.of(Position.values())
                .filter(p -> p.getOrderNum().equals(orderNum))
                .findFirst()
                .orElse(null);
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrderNum() {
        return orderNum;
    }
}
