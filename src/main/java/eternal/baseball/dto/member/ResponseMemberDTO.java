package eternal.baseball.dto.member;

import eternal.baseball.dto.util.BindingErrorDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ResponseMemberDTO {

    private boolean error;
    private List<BindingErrorDTO> bindingErrors;
    private MemberDTO member;
}
