package eternal.baseball.global.extension;

import lombok.Data;

@Data
public class ConfirmMessage {

    private String question;
    private String alertMessage;
    private String redirectURI;
    private String refererURI;

    public ConfirmMessage(String question, String alertMessage, String redirectURI, String refererURI) {
        this.question = question;
        this.alertMessage = alertMessage;
        this.redirectURI = redirectURI;
        this.refererURI = refererURI;
    }
}
