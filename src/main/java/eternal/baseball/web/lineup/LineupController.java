package eternal.baseball.web.lineup;

import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.lineup.Lineup;
import eternal.baseball.domain.lineup.LineupRepository;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.TeamRepository;
import eternal.baseball.domain.teamMember.TeamMember;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import eternal.baseball.web.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public String writeLineupForm(@PathVariable Long teamId,
                                  @ModelAttribute("lineupForm") LineupFormDto lineupForm,
                                  Model model,
                                  HttpServletRequest request) {

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[writeLineupForm] init lineupForm={}", lineupForm);

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMember> teamMembers = (ArrayList<TeamMember>) teamMemberRepository.findByTeamId(teamId);
            lineupForm = new LineupFormDto(teamMembers);
            log.info("[writeLineupForm] ====lineupForm is null====");
        }
        log.info("[writeLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", new LineupChangeCard());
        model.addAttribute("teamId", teamId);

        //Post 메소드 실행에 쓰기 위해 session 에 담는다
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LINEUP_CARD, lineupForm);
        return "lineup/writeLineupForm";
    }

    /**
     * 라인업 작성 페이지 타순 교체
     */
    @PostMapping("/{teamId}/changeOrder")
    public String changeOrder(@PathVariable Long teamId,
                              @ModelAttribute("LineupChangeCard") LineupChangeCard lineupChangeCard,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        log.info("[changeOrder] lineupChangeCard={}", lineupChangeCard);
        if (bindingResult.hasErrors()) {
            return "lineup/writeLineupForm";
        }

        LineupFormDto lineupForm = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        List<Boolean> trueList = lineupChangeCard.isTrueList();

        ArrayList<PlayerForm> startingPlayers = lineupForm.getStartingPlayers();
        ArrayList<PlayerForm> benchPlayers = lineupForm.getBenchPlayers();

        PlayerForm player1 = null;
        int player1OrderIndex = 0;
        for (Boolean tf : trueList) {
            if (tf) {
                player1 = startingPlayers.get(player1OrderIndex);
                break;
            }
            player1OrderIndex++;
        }

        if (player1 == null) {
            //주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다
            //jquery 로 이미 처리한 부분
            log.info("[changeOrder] ====player1 is null====");
            bindingResult.reject("needStarting", "주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult;", bindingResult);
            redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
            return "redirect:/lineup/" + teamId + "/create";
        }

        PlayerForm player2 = null;
        int player2OrderIndex = 0;
        for (int i = player1OrderIndex + 1; i < 9; i++) {
            if (trueList.get(i)) {
                player2 = startingPlayers.get(i);
                player2OrderIndex = i;
                break;
            }
        }

        if (player2 == null) {
            if (lineupChangeCard.getBench() == null) {
                //2명의 선수를 선택해야 합니다
                log.info("[changeOrder] ====player2 is null====");
                bindingResult.reject("needTwoPlayers", "2명의 선수를 선택해야 합니다");
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult;", bindingResult);
                redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
                return "redirect:/lineup/" + teamId + "/create";
            }
            player2OrderIndex = lineupChangeCard.getBench() - 1;
            player2 = benchPlayers.get(player2OrderIndex - 9);
        }

        log.info("[changeOrder] player1={}", player1);
        log.info("[changeOrder] player2={}", player2);

        String player1Position = player1.getPosition();
        Integer player1OrderNum = player1.getOrderNum();

        String player2Position = player2.getPosition();
        Integer player2OrderNum = player2.getOrderNum();

        player2.setPosition(player1Position);
        player2.setOrderNum(player1OrderNum);

        player1.setPosition(player2Position);
        player1.setOrderNum(player2OrderNum);

        //교체
        startingPlayers.set(player1OrderIndex, player2);
        if (player2OrderIndex < 9) {
            startingPlayers.set(player2OrderIndex, player1);
        } else {
            benchPlayers.set(player2OrderIndex - 9, player1);
        }

        log.info("[changeOrder] after player1={}", player1);
        log.info("[changeOrder] after player2={}", player2);

        lineupForm.setStartingPlayers(startingPlayers);
        lineupForm.setBenchPlayers(benchPlayers);

        model.addAttribute("teamId", teamId);

        //redirect 설정
        //세션에 담지 않고 redirectAttributes 로 플래시 세션에 담는다. 리다리엑트 후 소멸한다
        redirectAttributes.addFlashAttribute("lineupForm", lineupForm);

        return "redirect:/lineup/" + teamId + "/create";
    }

    /**
     * 라인업 작성 페이지 포지션 교체
     */
    @PostMapping("/{teamId}/changePosition")
    public String changePosition(@PathVariable Long teamId,
                                 @ModelAttribute("LineupChangeCard") LineupChangeCard lineupChangeCard,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        LineupFormDto lineupForm = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        List<Boolean> trueList = lineupChangeCard.isTrueList();
        log.info("[changePosition] lineupForm={}", lineupForm);

        ArrayList<PlayerForm> startingPlayers = lineupForm.getStartingPlayers();

        PlayerForm player1 = null;
        int player1OrderIndex = 0;
        for (Boolean tf : trueList) {
            if (tf) {
                player1 = startingPlayers.get(player1OrderIndex);
                break;
            }
            player1OrderIndex++;
        }

        if (player1 == null) {
            //주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다
            //jquery 로 이미 처리한 부분
            log.info("[changePosition] ====player1 is null====");
            bindingResult.reject("needStarting", "주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult;", bindingResult);
            redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
            return "redirect:/lineup/" + teamId + "/create";
        }

        PlayerForm player2 = null;
        int player2OrderIndex = 0;
        for (int i = player1OrderIndex + 1; i < 9; i++) {
            if (trueList.get(i)) {
                player2 = startingPlayers.get(i);
                player2OrderIndex = i;
                break;
            }
        }

        if (player2 == null) {
            //주전 라인업에서 2명의 선수를 선택해야 합니다
            log.info("[changePosition] ====player2 is null====");
            bindingResult.reject("needTwoStartingPlayers", "2명의 선수를 선택해야 합니다");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult;", bindingResult);
            redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
            return "redirect:/lineup/" + teamId + "/create";
        }

        log.info("[changePosition] player1={}", player1);
        log.info("[changePosition] player2={}", player2);

        String player1Position = player1.getPosition();
        String player2Position = player2.getPosition();

        player1.setPosition(player2Position);
        player2.setPosition(player1Position);

        //교체
        startingPlayers.set(player1OrderIndex, player1);
        startingPlayers.set(player2OrderIndex, player2);

        log.info("[changePosition] after player1={}", player1);
        log.info("[changePosition] after player2={}", player2);

        lineupForm.setStartingPlayers(startingPlayers);

        //redirect 설정
        redirectAttributes.addFlashAttribute("lineupForm", lineupForm);

        model.addAttribute("teamId", teamId);
        return "redirect:/lineup/" + teamId + "/create";
    }

    /**
     * 라인업 작성
     */
    @PostMapping("/{teamId}/create")
    public String writeLineup(@PathVariable Long teamId,
                              HttpServletRequest request,
                              Model model) {

        LineupFormDto lineupFormDto = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        Member loginMember = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        log.info("[writeLineup] lineupForm={}", lineupFormDto);

        Lineup lineup = new Lineup();

        //오우쉣
        LineupForm lineupForm = toLineupForm(lineupFormDto);
        log.info("[writeLineup] convert lineupForm success!!");

        TeamMember loginTeamMember = teamMemberRepository.findByTeamId(teamId).stream()
                .filter(tm -> tm.getMember().equals(loginMember))
                .findFirst()
                .orElse(null);

        log.info("[writeLineup] loginTeamMember={}", loginTeamMember);

        lineup.setTeam(teamRepository.findByTeamId(teamId));
        lineup.setWriter(loginTeamMember);
        lineup.setLineupCard(lineupForm.getStartingPlayers());
        lineup.setBench(lineupForm.getBenchPlayers());

        lineupRepository.save(lineup);
        log.info("[writeLineup] lineup={}", lineup);

        return "redirect:/lineup/" + teamId + "/list";
    }

    @ModelAttribute("lineupNumber")
    public List<String> lineupNumber() {
        List<String> lineupNumber = new ArrayList<>();
        lineupNumber.add("one");
        lineupNumber.add("two");
        lineupNumber.add("three");
        lineupNumber.add("four");
        lineupNumber.add("five");
        lineupNumber.add("six");
        lineupNumber.add("seven");
        lineupNumber.add("eight");
        lineupNumber.add("nine");
        lineupNumber.add("bench");
        return lineupNumber;
    }

    public LineupForm toLineupForm(LineupFormDto lineupFormDto) {

        LineupForm lineupForm = new LineupForm();
        ArrayList<PlayerForm> startingPlayers = lineupFormDto.getStartingPlayers();
        ArrayList<PlayerForm> benchPlayers = lineupFormDto.getBenchPlayers();

        ArrayList<Player> starting = (ArrayList<Player>) startingPlayers.stream()
                .map(pf -> new Player(teamMemberRepository.findByTeamMemberId(pf.getTeamMemberId()), Position.fromDescription(pf.getPosition()), pf.getOrderNum()))
                .collect(Collectors.toList());

        ArrayList<Player> bench = (ArrayList<Player>) benchPlayers.stream()
                .map(pf -> new Player(teamMemberRepository.findByTeamMemberId(pf.getTeamMemberId()), Position.fromDescription(pf.getPosition()), pf.getOrderNum()))
                .collect(Collectors.toList());

        log.info("[toLineupForm] starting={}", starting);
        log.info("[toLineupForm] bench={}", bench);

        lineupForm.setStartingPlayers(starting);
        lineupForm.setBenchPlayers(bench);

        return lineupForm;
    }
}
