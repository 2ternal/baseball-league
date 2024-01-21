package eternal.baseball.dto.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ResponseDataDTO<T> {

    private boolean error;
    private List<BindingErrorDTO> bindingErrors;
    private T data;
}
