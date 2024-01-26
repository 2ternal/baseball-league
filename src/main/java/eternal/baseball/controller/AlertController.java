package eternal.baseball.controller;

import eternal.baseball.global.extension.AlertMessageBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/alert")
public class AlertController {

    @GetMapping("/notInclude")
    public String notTeamMemberAlert(Model model, @RequestParam(defaultValue = "/") String redirectURL) {

        AlertMessageBox message = new AlertMessageBox("팀에 소속되어 있지 않습니다", redirectURL);
        model.addAttribute("message", message);

        log.info("[notTeamMemberAlert] alert 메세지 발생={}", redirectURL);
        return "template/alert";
    }
}
