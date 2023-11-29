package eternal.baseball.domain.custom;

public enum Position {

    FIRST("1루수"), SECOND("2루수"), THIRD("3루수"), SHORT("유격수"),
    LEFT("좌익수"), CENTER("중견수"), RIGHT("우익수"),
    CATCHER("포수"), PITCHER("투수");

    private final String description;

    Position(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
