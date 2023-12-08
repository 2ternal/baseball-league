package eternal.baseball.web.extension;

import lombok.Data;

@Data
public class AlertMessage {

    private String message;
    private String redirectURI;

    public AlertMessage(String message, String redirectURI) {
        this.message = message;
        this.redirectURI = redirectURI;
    }
}
