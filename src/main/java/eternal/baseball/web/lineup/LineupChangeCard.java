package eternal.baseball.web.lineup;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LineupChangeCard {
    private boolean one;
    private boolean two;
    private boolean three;
    private boolean four;
    private boolean five;
    private boolean six;
    private boolean seven;
    private boolean eight;
    private boolean nine;
    private Integer bench;

    public LineupChangeCard() {
    }

    public List<Boolean> isTrueList() {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(this.one);
        booleans.add(this.two);
        booleans.add(this.three);
        booleans.add(this.four);
        booleans.add(this.five);
        booleans.add(this.six);
        booleans.add(this.seven);
        booleans.add(this.eight);
        booleans.add(this.nine);
        return booleans;
    }
}
