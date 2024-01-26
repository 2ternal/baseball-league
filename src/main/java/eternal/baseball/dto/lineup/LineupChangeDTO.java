package eternal.baseball.dto.lineup;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineupChangeDTO {

    public LineupFormDTO lineupForm;
    public Boolean status;
    public String errCode;
    public String errMessage;
}
