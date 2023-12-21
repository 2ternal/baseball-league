package eternal.baseball.domain.custom;

import java.util.stream.Stream;

public enum Position {

    FIRST("1루수", 3, "FIRST"), SECOND("2루수", 4, "SECOND"), THIRD("3루수", 5, "THIRD"), SHORT("유격수", 6, "SHORT"),
    LEFT("좌익수", 7, "LEFT"), CENTER("중견수", 8, "CENTER"), RIGHT("우익수", 9, "RIGHT"),
    CATCHER("포수", 2, "CATCHER"), PITCHER("투수", 1, "PITCHER"), DH("지명타자", 10, "DH"), NONE("없음", 0, "NONE");

    public final String description;
    public final Integer orderNum;
    public final String eng;

    Position(String description, Integer orderNum, String eng) {
        this.description = description;
        this.orderNum = orderNum;
        this.eng = eng;
    }

    public static Position fromDescription(String description) {
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

    public static Position fromEng(String eng) {
        return Stream.of(Position.values())
                .filter(p -> p.getEng().equals(eng))
                .findFirst()
                .orElse(null);
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public String getEng() {
        return eng;
    }
}
