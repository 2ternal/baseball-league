package eternal.baseball.web.lineup;

import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.lineup.Lineup;
import eternal.baseball.domain.lineup.LineupRepository;
import eternal.baseball.domain.member.Member;
import eternal.baseball.domain.team.TeamRepository;
import eternal.baseball.domain.teamMember.TeamMember;
import eternal.baseball.domain.teamMember.TeamMemberRepository;
import eternal.baseball.web.extension.AlertMessage;
import eternal.baseball.web.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @GetMapping("/{teamId}/{lineupId}")
    public String lineup(@PathVariable Long teamId, @PathVariable Long lineupId, Model model) {
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
                                  @ModelAttribute("errCode") String errCode,
                                  @ModelAttribute("errMessage") String errMessage,
                                  @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                  BindingResult bindingResult,
                                  HttpServletRequest request,
                                  Model model) {


        log.info("[writeLineupForm] init lineupForm={}", lineupForm);
        log.info("[writeLineupForm] init errCode={}", errCode);
        log.info("[writeLineupForm] init errCode isEmpty={}", errCode.isEmpty());
        log.info("[writeLineupForm] init errMessage={}", errMessage);
        //
        if (!errCode.isEmpty()) {
            bindingResult.reject(errCode, errMessage);
            log.info("[writeLineupForm] bindingResult={}", bindingResult);
            return "lineup/writeLineupForm";
        }
        //
        Member loginMember = getLoginMember(request);
        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamId);

        if (loginTeamMember.getMemberShip().getGrade() > 3) {
            String redirectURI = request.getHeader("REFERER");
            AlertMessage message = new AlertMessage("라인업은 코치등급부터 작성할 수 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMember> teamMembers = (ArrayList<TeamMember>) teamMemberRepository.findByTeamId(teamId);
            if (teamMembers.size() < 9) {
                log.info("[writeLineupForm] be short of teamMember={}", teamMembers.size());
                String redirectURI = request.getHeader("REFERER");
                AlertMessage message = new AlertMessage("팀원이 부족합니다", redirectURI);
                model.addAttribute("message", message);
                return "template/alert";
            }

            lineupForm = new LineupFormDto(teamMembers);
        }

        lineupChangeCard = new LineupChangeCard();
        lineupChangeCard.setLineupName(lineupForm.getLineupName());

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[writeLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", lineupChangeCard);
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
                              @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {

        String redirectURI = request.getHeader("REFERER");
        log.info("[changeOrder] REFERER redirectURI={}", redirectURI);

        log.info("[changeOrder] lineupChangeCard={}", lineupChangeCard);
        if (bindingResult.hasErrors()) {
            return "lineup/writeLineupForm";
        }

        LineupFormDto lineupForm = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        List<Boolean> trueList = lineupChangeCard.isTrueList();
        lineupForm.setLineupName(lineupChangeCard.getLineupName());

        ArrayList<PlayerDto> startingPlayers = lineupForm.getStartingPlayers();
        ArrayList<PlayerDto> benchPlayers = lineupForm.getBenchPlayers();

        PlayerDto player1 = null;
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
            redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
            redirectAttributes.addFlashAttribute("errCode", "needStarting");
            redirectAttributes.addFlashAttribute("errMessage", "주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다");

//            return "redirect:/lineup/" + teamId + "/create";
            return "redirect:" + redirectURI;
        }

        PlayerDto player2 = null;
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
                redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
                redirectAttributes.addFlashAttribute("errCode", "needTwoPlayers");
                redirectAttributes.addFlashAttribute("errMessage", "2명의 선수를 선택해야 합니다");

//                return "redirect:/lineup/" + teamId + "/create";
                return "redirect:" + redirectURI;
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

        //redirect 설정
        //세션에 담지 않고 redirectAttributes 로 플래시 세션에 담는다. 리다리엑트 후 소멸한다
        redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
        redirectAttributes.addAttribute("status", true);

//        return "redirect:/lineup/" + teamId + "/create";
        return "redirect:" + redirectURI;
    }

    /**
     * 라인업 작성 페이지 포지션 교체
     */
    @PostMapping("/{teamId}/changePosition")
    public String changePosition(@PathVariable Long teamId,
                                 @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        String redirectURI = request.getHeader("REFERER");
        log.info("[changePosition] REFERER redirectURI={}", redirectURI);

        if (bindingResult.hasErrors()) {
            return "lineup/writeLineupForm";
        }

        LineupFormDto lineupForm = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        List<Boolean> trueList = lineupChangeCard.isTrueList();
        log.info("[changePosition] lineupForm={}", lineupForm);
        lineupForm.setLineupName(lineupChangeCard.getLineupName());

        ArrayList<PlayerDto> startingPlayers = lineupForm.getStartingPlayers();

        PlayerDto player1 = null;
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
            redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
            redirectAttributes.addFlashAttribute("errCode", "needStarting");
            redirectAttributes.addFlashAttribute("errMessage", "주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다");

//            return "redirect:/lineup/" + teamId + "/create";
            return "redirect:" + redirectURI;
        }

        PlayerDto player2 = null;
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
            //2명의 선수를 선택해야 합니다
            log.info("[changePosition] ====player2 is null====");
            redirectAttributes.addFlashAttribute("lineupForm", lineupForm);
            redirectAttributes.addFlashAttribute("errCode", "needTwoStartingPlayers");
            redirectAttributes.addFlashAttribute("errMessage", "주전 라인업에서 2명의 선수를 선택해야 합니다");

//            return "redirect:/lineup/" + teamId + "/create";
            return "redirect:" + redirectURI;
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
        redirectAttributes.addAttribute("status", true);

//        return "redirect:/lineup/" + teamId + "/create";
        return "redirect:" + redirectURI;
    }

    /**
     * 라인업 작성
     */
    @PostMapping("/{teamId}/create")
    public String writeLineup(@PathVariable Long teamId,
                              @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              Model model) {

        log.info("[writeLineup] lineupChangeCard={}", lineupChangeCard);
        LineupFormDto lineupForm = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamId", teamId);
            log.info("[writeLineup] bindingResult={}", bindingResult);
            return "lineup/writeLineupForm";
        }

        Member loginMember = getLoginMember(request);
        log.info("[writeLineup] lineupForm={}", lineupForm);

        Lineup lineup = new Lineup();

        //라인업 저장을 위한 convert 과정
        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);
        log.info("[writeLineup] convert lineupForm success!!");

        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamId);

        log.info("[writeLineup] loginTeamMember={}", loginTeamMember);

        lineup.setTeam(teamRepository.findByTeamId(teamId));
        lineup.setWriter(loginTeamMember);
        lineup.setStarting(starting);
        lineup.setBench(bench);
        lineup.setLineupName(lineupChangeCard.getLineupName());
        lineup.setUpdateTime(new Date());

        lineupRepository.save(lineup);
        log.info("[writeLineup] lineup={}", lineup);

        return "redirect:/lineup/" + teamId + "/list";
    }

    /**
     * 라인업 수정 폼
     */
    @GetMapping("/{teamId}/{lineupId}/edit")
    public String editLineupForm(@PathVariable Long teamId,
                                 @PathVariable Long lineupId,
                                 @ModelAttribute("lineupForm") LineupFormDto lineupForm,
                                 @ModelAttribute("errCode") String errCode,
                                 @ModelAttribute("errMessage") String errMessage,
                                 @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 Model model) {

        log.info("[editLineupForm] init lineupForm={}", lineupForm);
        log.info("[editLineupForm] init errCode={}", errCode);
        log.info("[editLineupForm] init errCode isEmpty={}", errCode.isEmpty());
        log.info("[editLineupForm] init errMessage={}", errMessage);

        if (!errCode.isEmpty()) {
            bindingResult.reject(errCode, errMessage);
            log.info("[editLineupForm] bindingResult={}", bindingResult);
            return "lineup/editLineupForm";
        }

        Member loginMember = getLoginMember(request);
        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamId);

        if (loginTeamMember.getMemberShip().getGrade() > 3) {
            String redirectURI = request.getHeader("REFERER");
            AlertMessage message = new AlertMessage("라인업은 코치등급부터 작성할 수 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMember> teamMembers = (ArrayList<TeamMember>) teamMemberRepository.findByTeamId(teamId);
            if (teamMembers.size() < 9) {
                log.info("[editLineupForm] be short of teamMember={}", teamMembers.size());
                String redirectURI = request.getHeader("REFERER");
                AlertMessage message = new AlertMessage("팀원이 부족합니다", redirectURI);
                model.addAttribute("message", message);
                return "template/alert";
            }

            Lineup lineup = lineupRepository.findByLineupId(lineupId);

            lineupForm = new LineupFormDto(lineup);
            log.info("[editLineupForm] new lineupForm={}", lineupForm);

        }

        lineupChangeCard = new LineupChangeCard();
        lineupChangeCard.setLineupName(lineupForm.getLineupName());

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[editLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupId", lineupId);
        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", lineupChangeCard);
        model.addAttribute("teamId", teamId);

        //Post 메소드 실행에 쓰기 위해 session 에 담는다
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LINEUP_CARD, lineupForm);
        return "lineup/editLineupForm";
    }

    /**
     * 라인업 수정
     */
    @PostMapping("/{teamId}/{lineupId}/edit")
    public String editLineup(@PathVariable Long teamId,
                             @PathVariable Long lineupId,
                             @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             Model model) {

        LineupFormDto lineupForm = (LineupFormDto) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        log.info("[editLineup] lineupForm={}", lineupForm);

        Member loginMember = getLoginMember(request);

        Lineup lineup = new Lineup();

        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);
        log.info("[editLineup] convert lineupForm success!!");

        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamId);

        lineup.setLineupId(lineupId);
        lineup.setTeam(teamRepository.findByTeamId(teamId));
        lineup.setWriter(loginTeamMember);
        lineup.setStarting(starting);
        lineup.setBench(bench);
        lineup.setLineupName(lineupChangeCard.getLineupName());
        lineup.setUpdateTime(new Date());

        lineupRepository.edit(lineupId, lineup);
        log.info("[editLineup] lineup={}", lineup);

        return "redirect:/lineup/" + teamId + "/list";
    }


    /**
     * 라인업 삭제
     */
    @PostMapping("/{teamId}/{lineupId}/delete")
    public String deleteLineup(@PathVariable Long teamId,
                               @PathVariable Long lineupId,
                               HttpServletRequest request,
                               Model model) {

        Member loginMember = getLoginMember(request);
        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamId(loginMember.getMemberId(), teamId);
        Lineup lineup = lineupRepository.findByLineupId(lineupId);

        if (!loginTeamMember.getMemberShip().equals(TeamMemberShip.OWNER) && loginTeamMember != lineup.getWriter()) {
            log.info("[deleteLineup] loginTeamMember != lineup.getWriter()");
            String redirectURI = request.getHeader("REFERER");
            AlertMessage message = new AlertMessage("이 라인업을 삭제할 권한이 없습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        log.info("[deleteLineup] deleteLineup!!");
        lineupRepository.deleteLineup(lineupId);
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

    private static Member getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    public ArrayList<Player> getStarting(LineupFormDto lineupFormDto) {

        ArrayList<PlayerDto> startingPlayers = lineupFormDto.getStartingPlayers();

        ArrayList<Player> starting = (ArrayList<Player>) startingPlayers.stream()
                .map(pf -> new Player(teamMemberRepository.findByTeamMemberId(pf.getTeamMemberId()), Position.fromDescription(pf.getPosition()), pf.getOrderNum()))
                .collect(Collectors.toList());

        log.info("[getStarting] starting={}", starting);

        return starting;
    }

    public ArrayList<Player> getBench(LineupFormDto lineupFormDto) {

        ArrayList<PlayerDto> benchPlayers = lineupFormDto.getBenchPlayers();

        ArrayList<Player> bench = (ArrayList<Player>) benchPlayers.stream()
                .map(pf -> new Player(teamMemberRepository.findByTeamMemberId(pf.getTeamMemberId()), Position.fromDescription(pf.getPosition()), pf.getOrderNum()))
                .collect(Collectors.toList());

        log.info("[getBench] bench={}", bench);

        return bench;
    }
}
