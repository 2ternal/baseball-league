package eternal.baseball.dto.lineup;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LineupChangeCard {

    private String teamCode;
    @NotEmpty
    private String lineupName;
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
