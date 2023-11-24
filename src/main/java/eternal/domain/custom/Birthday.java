package eternal.domain.custom;

import lombok.Data;

@Data
public class Birthday {

    private String year;
    private String month;
    private String day;

    public Birthday() {
    }

    public Birthday(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
