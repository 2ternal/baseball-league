package eternal.baseball.global.extension;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessageBox {

    private String message;
    private String redirectURI;
}
