package eternal.baseball.dto.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BindingErrorDTO {

    private String errorField;
    private String errorCode;
    private String errorMessage;
}
