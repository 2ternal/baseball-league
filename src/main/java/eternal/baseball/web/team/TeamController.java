package eternal.baseball.web.team;

import eternal.baseball.domain.team.Team;
import eternal.baseball.domain.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamRepository teamRepository;

    @GetMapping("/teams")
    public String teams(Model model) {
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("teams", teams);
        return "team/teams";
    }

    @GetMapping("/createTeam")
    public String createTeamForm(Model model) {
        model.addAttribute("createTeamForm", new CreateTeamForm());
        return "team/createTeamForm";
    }

    @PostMapping("/createTeam")
    public String createTeam(@Validated @ModelAttribute("createTeamForm") CreateTeamForm createTeamForm,
                             BindingResult bindingResult) {

        // 팀명 중복 검증
        if (!ObjectUtils.isEmpty(teamRepository.findByTeamName(createTeamForm.getTeamName()))) {
            log.info("  duplicateTeamName={}", createTeamForm.getTeamName());
            bindingResult.reject("duplicateTeamName", "중복되는 팀명 입니다");
            log.info("bindingResult={}", bindingResult);
        }

        // 팀 코드 중복 검증
        if (!ObjectUtils.isEmpty(teamRepository.findByTeamCode(createTeamForm.getTeamCode()))) {
            log.info("  duplicateTeamCode={}", createTeamForm.getTeamCode());
            bindingResult.reject("duplicateTeamCode", "중복되는 팀 코드 입니다");
            log.info("bindingResult={}", bindingResult);
        }

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "team/createTeamForm";
        }

        Team team = new Team();
        team.createTeamFormToTeam(createTeamForm);

        teamRepository.save(team);

        return "redirect:teams";
    }
}
