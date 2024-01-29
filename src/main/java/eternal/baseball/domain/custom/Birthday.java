package eternal.baseball.domain.custom;

import lombok.*;

@Getter
@NoArgsConstructor
public class Birthday {

    private Integer year;
    private Integer month;
    private Integer day;

    @Override
    public String toString() {
        return year + "년 " + month + "월 " + day + "일";
    }

    @Builder
    public Birthday(Integer year, Integer month, Integer day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
