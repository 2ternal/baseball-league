package eternal.baseball.web.lineup;

import eternal.baseball.domain.lineup.Lineup;
import eternal.baseball.domain.lineup.LineupRepository;
import eternal.baseball.domain.team.TeamRepository;
import eternal.baseball.domain.teamMember.TeamMember;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lineup")
public class LineupController {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final LineupRepository lineupRepository;

    /**
     * 라인업 상세 페이지
     */
    @GetMapping("/{lineupId}")
    public String lineup(@PathVariable Long lineupId, Model model) {
        Lineup lineup = lineupRepository.findByLineupId(lineupId);
        model.addAttribute("lineup", lineup);
        return "lineup/lineup";
    }

    /**
     * 라인업 목록 페이지
     */
    @GetMapping("/{teamId}/list")
    public String lineupList(@PathVariable Long teamId, Model model) {
        List<Lineup> teamLineupList = lineupRepository.findByTeamId(teamId);
        model.addAttribute("lineupList", teamLineupList);
        model.addAttribute("teamId", teamId);
        return "lineup/lineupList";
    }

    /**
     * 라인업 작성 페이지
     */
    @GetMapping("/{teamId}/create")
    public String writeLineupForm(@PathVariable Long teamId, Model model) {
        ArrayList<TeamMember> teamMembers = (ArrayList<TeamMember>) teamMemberRepository.findByTeamId(teamId);
        LineupForm lineupForm = new LineupForm(teamMembers);
        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("teamId", teamId);
        log.info("[writeLineupForm] lineupForm={}", lineupForm);
        return "lineup/writeLineupForm";
    }

    /**
     * 라인업 작성
     */
    @PostMapping("/{teamId}/create")
    public String writeLineup(@PathVariable Long teamId,
                              @ModelAttribute("lineupForm") LineupForm lineupForm,
                              BindingResult bindingResult,
                              Model model) {
        log.info("[writeLineup] lineupForm={}", lineupForm);

        /**
         * todo
         * - teamMember 에 중복 불가능한 닉네임 혹은 멤버 이름 추가
         * - lineupForm 필드 개선
         * - writeLineupForm.html 개선
         * - 마저 구현하지 못한 기능들 구현
         */

        return "redirect:/team/" + teamId;
    }

    /**
     * 라인업 작성 페이지 타순 교체
     */
    @PostMapping("/{teamId}/changeOrder")
    public String changeOrder(@PathVariable Long teamId,
                              @ModelAttribute("lineupForm") LineupForm lineupForm,
                              BindingResult bindingResult,
                              Model model) {
        log.info("[changeOrder] lineupForm={}", lineupForm);
//        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("teamId", teamId);
        return "redirect:/lineup/" + teamId + "/create";
    }

    /**
     * 라인업 작성 페이지 포지션 교체
     */
    @PostMapping("/{teamId}/changePosition")
    public String changePosition(@PathVariable Long teamId,
                                 @ModelAttribute("lineupForm") LineupForm lineupForm,
                                 BindingResult bindingResult,
                                 Model model) {
        log.info("[changePosition] lineupForm={}", lineupForm);
//        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("teamId", teamId);
        return "redirect:/lineup/" + teamId + "/create";
    }
}
