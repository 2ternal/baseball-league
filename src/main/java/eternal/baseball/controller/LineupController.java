package eternal.baseball.controller;

import eternal.baseball.domain.Member;
import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.domain.custom.TeamMemberShip;
import eternal.baseball.domain.Lineup;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.dto.lineup.LineupDTO;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.player.PlayerDTO;
import eternal.baseball.repository.TeamMemberRepository;
import eternal.baseball.repository.TeamRepository;
import eternal.baseball.service.LineupService;
import eternal.baseball.service.TeamMemberService;
import eternal.baseball.service.TeamService;
import eternal.baseball.global.extension.AlertMessage;
import eternal.baseball.dto.lineup.LineupChangeCard;
import eternal.baseball.dto.lineup.LineupFormDTO;
import eternal.baseball.global.constant.SessionConst;
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

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final LineupService lineupService;

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 라인업 상세 페이지
     */
    @GetMapping("/{teamCode}/{lineupId}")
    public String lineup(@PathVariable String teamCode, @PathVariable Long lineupId, Model model) {
        LineupDTO lineup = lineupService.findLineup2(lineupId);
        model.addAttribute("lineup", lineup);
        return "lineup/lineup";
    }

    /**
     * 라인업 목록 페이지
     */
    @GetMapping("/{teamCode}/list")
    public String lineupList(@PathVariable String teamCode, Model model) {
        List<Lineup> teamLineupList = lineupService.findTeamLineupList(teamCode);
        model.addAttribute("lineupList", teamLineupList);
        model.addAttribute("teamCode", teamCode);
        return "lineup/lineupList";
    }

    /**
     * 라인업 작성 페이지
     */
    @GetMapping("/{teamCode}/create")
    public String writeLineupForm(@PathVariable String teamCode,
                                  @ModelAttribute("lineupForm") LineupFormDTO lineupForm,
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

        if (!errCode.isEmpty()) {
            bindingResult.reject(errCode, errMessage);
            log.info("[writeLineupForm] bindingResult={}", bindingResult);
            return "lineup/writeLineupForm";
        }

        MemberDTO loginMemberDTO = getLoginMember(request);
        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        if (loginTeamMember.getMemberShip().getGrade() > 3) {
            String redirectURI = request.getHeader("REFERER");
            AlertMessage message = new AlertMessage("라인업은 코치등급부터 작성할 수 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMember> teamMembers = teamMemberService.findTeamMembers(teamCode);
            if (teamMembers.size() < 9) {
                log.info("[writeLineupForm] be short of teamMember={}", teamMembers.size());
                String redirectURI = request.getHeader("REFERER");
                AlertMessage message = new AlertMessage("팀원이 부족합니다", redirectURI);
                model.addAttribute("message", message);
                return "template/alert";
            }

            lineupForm = new LineupFormDTO(teamMembers);
        }

        lineupChangeCard = new LineupChangeCard();
        lineupChangeCard.setLineupName(lineupForm.getLineupName());

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[writeLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", lineupChangeCard);
        model.addAttribute("teamCode", teamCode);

        //Post 메소드 실행에 쓰기 위해 session 에 담는다
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LINEUP_CARD, lineupForm);
        return "lineup/writeLineupForm";
    }

    /**
     * 라인업 작성 페이지 타순 교체
     */
    @PostMapping("/{teamCode}/changeOrder")
    public String changeOrder(@PathVariable String teamCode,
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

        LineupFormDTO lineupForm = (LineupFormDTO) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        List<Boolean> trueList = lineupChangeCard.isTrueList();
        lineupForm.setLineupName(lineupChangeCard.getLineupName());

        ArrayList<PlayerDTO> startingPlayers = lineupForm.getStartingPlayers();
        ArrayList<PlayerDTO> benchPlayers = lineupForm.getBenchPlayers();

        PlayerDTO player1 = null;
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
            redirectAttributes.addFlashAttribute("lineupChangeCard", lineupChangeCard);

            return "redirect:" + redirectURI;
        }

        PlayerDTO player2 = null;
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
                redirectAttributes.addFlashAttribute("lineupChangeCard", lineupChangeCard);

                return "redirect:" + redirectURI;
            }
            player2OrderIndex = lineupChangeCard.getBench() - 1;
            player2 = benchPlayers.get(player2OrderIndex - 9);
        }

        log.info("[changeOrder] player1={}", player1);
        log.info("[changeOrder] player2={}", player2);

        Position player1Position = player1.getPosition();
        Integer player1OrderNum = player1.getOrderNum();

        Position player2Position = player2.getPosition();
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
        redirectAttributes.addFlashAttribute("status", "true");

        return "redirect:" + redirectURI;
    }

    /**
     * 라인업 작성 페이지 포지션 교체
     */
    @PostMapping("/{teamCode}/changePosition")
    public String changePosition(@PathVariable String teamCode,
                                 @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        String redirectURI = request.getHeader("REFERER");
        log.info("[changePosition] REFERER redirectURI={}", redirectURI);

        if (bindingResult.hasErrors()) {
            return "lineup/writeLineupForm";
        }

        LineupFormDTO lineupForm = (LineupFormDTO) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        List<Boolean> trueList = lineupChangeCard.isTrueList();
        log.info("[changePosition] lineupForm={}", lineupForm);
        lineupForm.setLineupName(lineupChangeCard.getLineupName());

        ArrayList<PlayerDTO> startingPlayers = lineupForm.getStartingPlayers();

        PlayerDTO player1 = null;
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
            redirectAttributes.addFlashAttribute("lineupChangeCard", lineupChangeCard);

            return "redirect:" + redirectURI;
        }

        PlayerDTO player2 = null;
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
            redirectAttributes.addFlashAttribute("lineupChangeCard", lineupChangeCard);

            return "redirect:" + redirectURI;
        }

        log.info("[changePosition] player1={}", player1);
        log.info("[changePosition] player2={}", player2);

        Position player1Position = player1.getPosition();
        Position player2Position = player2.getPosition();

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
        redirectAttributes.addFlashAttribute("status", "true");

        return "redirect:" + redirectURI;
    }

    /**
     * 라인업 작성
     */
    @PostMapping("/{teamCode}/create")
    public String writeLineup(@PathVariable String teamCode,
                              @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              Model model) {

        log.info("[writeLineup] lineupChangeCard={}", lineupChangeCard);
        LineupFormDTO lineupForm = (LineupFormDTO) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamCode", teamCode);
            log.info("[writeLineup] bindingResult={}", bindingResult);
            return "lineup/writeLineupForm";
        }

        MemberDTO loginMemberDTO = getLoginMember(request);
        log.info("[writeLineup] lineupForm={}", lineupForm);

        Lineup lineup = new Lineup();

        //라인업 저장을 위한 convert 과정
        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);
        log.info("[writeLineup] convert lineupForm success!!");

        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        log.info("[writeLineup] loginTeamMember={}", loginTeamMember);

        lineup.setTeam(teamRepository.findByTeamCode(teamCode));
        lineup.setWriter(loginTeamMember);
        lineup.setStarting(starting);
        lineup.setBench(bench);
        lineup.setLineupName(lineupChangeCard.getLineupName());
        lineup.setUpdateTime(new Date());

        lineupService.createLineup(lineup);
        log.info("[writeLineup] lineup={}", lineup);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    /**
     * 라인업 수정 폼
     */
    @GetMapping("/{teamCode}/{lineupId}/edit")
    public String editLineupForm(@PathVariable String teamCode,
                                 @PathVariable Long lineupId,
                                 @ModelAttribute("lineupForm") LineupFormDTO lineupForm,
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

        MemberDTO loginMemberDTO = getLoginMember(request);
        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        if (loginTeamMember.getMemberShip().getGrade() > 3) {
            String redirectURI = request.getHeader("REFERER");
            AlertMessage message = new AlertMessage("라인업은 코치등급부터 작성할 수 있습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        if (lineupForm.getStartingPlayers() == null) {
            ArrayList<TeamMember> teamMembers = teamMemberService.findTeamMembers(teamCode);
            if (teamMembers.size() < 9) {
                log.info("[editLineupForm] be short of teamMember={}", teamMembers.size());
                String redirectURI = request.getHeader("REFERER");
                AlertMessage message = new AlertMessage("팀원이 부족합니다", redirectURI);
                model.addAttribute("message", message);
                return "template/alert";
            }

            Lineup lineup = lineupService.findLineup(lineupId);

            lineupForm = new LineupFormDTO(lineup);
            log.info("[editLineupForm] new lineupForm={}", lineupForm);

        }

        lineupChangeCard = new LineupChangeCard();
        lineupChangeCard.setLineupName(lineupForm.getLineupName());

        //Post 메소드에서 redirect 로 넘어왔다면 redirectAttributes 에 담긴 lineupForm 을 쓸 수 있다
        log.info("[editLineupForm] lineupForm={}", lineupForm);

        model.addAttribute("lineupId", lineupId);
        model.addAttribute("lineupForm", lineupForm);
        model.addAttribute("lineupChangeCard", lineupChangeCard);
        model.addAttribute("teamCode", teamCode);

        //Post 메소드 실행에 쓰기 위해 session 에 담는다
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LINEUP_CARD, lineupForm);
        return "lineup/editLineupForm";
    }

    /**
     * 라인업 수정
     */
    @PostMapping("/{teamCode}/{lineupId}/edit")
    public String editLineup(@PathVariable String teamCode,
                             @PathVariable Long lineupId,
                             @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             Model model) {

        LineupFormDTO lineupForm = (LineupFormDTO) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        log.info("[editLineup] lineupForm={}", lineupForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupId", lineupId);
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamCode", teamCode);
            log.info("[editLineup] bindingResult={}", bindingResult);
            return "lineup/editLineupForm";
        }

        MemberDTO loginMemberDTO = getLoginMember(request);

        Lineup lineup = new Lineup();

        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);
        log.info("[editLineup] convert lineupForm success!!");

        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        lineup.setLineupId(lineupId);
        lineup.setTeam(teamRepository.findByTeamCode(teamCode));
        lineup.setWriter(loginTeamMember);
        lineup.setStarting(starting);
        lineup.setBench(bench);
        lineup.setLineupName(lineupChangeCard.getLineupName());
        lineup.setUpdateTime(new Date());

        lineupService.editLineup(lineupId, lineup);
        log.info("[editLineup] lineup={}", lineup);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    /**
     * 라인업 다른 이름으로 저장
     */
    @PostMapping("/{teamCode}/{lineupId}/saveAs")
    public String saveAsDifferentLineup(@PathVariable String teamCode,
                                        @PathVariable Long lineupId,
                                        @Validated @ModelAttribute("lineupChangeCard") LineupChangeCard lineupChangeCard,
                                        BindingResult bindingResult,
                                        HttpServletRequest request,
                                        Model model) {

        LineupFormDTO lineupForm = (LineupFormDTO) request.getSession().getAttribute(SessionConst.LINEUP_CARD);
        log.info("[saveAsDifferentLineup] lineupForm={}", lineupForm);
        if (bindingResult.hasErrors()) {
            model.addAttribute("lineupId", lineupId);
            model.addAttribute("lineupForm", lineupForm);
            model.addAttribute("lineupChangeCard", lineupChangeCard);
            model.addAttribute("teamCode", teamCode);
            log.info("[saveAsDifferentLineup] bindingResult={}", bindingResult);
            return "lineup/editLineupForm";
        }

        MemberDTO loginMemberDTO = getLoginMember(request);

        Lineup lineup = new Lineup();

        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);
        log.info("[saveAsDifferentLineup] convert lineupForm success!!");

        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);

        lineup.setTeam(teamRepository.findByTeamCode(teamCode));
        lineup.setWriter(loginTeamMember);
        lineup.setStarting(starting);
        lineup.setBench(bench);
        lineup.setLineupName(lineupChangeCard.getLineupName());
        lineup.setUpdateTime(new Date());

        lineupService.createLineup(lineup);
        log.info("[saveAsDifferentLineup] lineup={}", lineup);

        return "redirect:/lineup/" + teamCode + "/list";
    }

    /**
     * 라인업 삭제
     */
    @PostMapping("/{teamCode}/{lineupId}/delete")
    public String deleteLineup(@PathVariable Long teamCode,
                               @PathVariable Long lineupId,
                               HttpServletRequest request,
                               Model model) {

        MemberDTO loginMemberDTO = getLoginMember(request);
        TeamMember loginTeamMember = teamMemberService.findTeamMember(loginMemberDTO.getMemberId(), teamCode);
        Lineup lineup = lineupService.findLineup(lineupId);

        if (!loginTeamMember.getMemberShip().equals(TeamMemberShip.OWNER) && loginTeamMember != lineup.getWriter()) {
            log.info("[deleteLineup] loginTeamMember != lineup.getWriter()");
            String redirectURI = request.getHeader("REFERER");
            AlertMessage message = new AlertMessage("이 라인업을 삭제할 권한이 없습니다", redirectURI);
            model.addAttribute("message", message);
            return "template/alert";
        }

        log.info("[deleteLineup] deleteLineup!!");
        lineupService.deleteLineup(lineupId);

        return "redirect:/lineup/" + teamCode + "/list";
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

    private static MemberDTO getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (MemberDTO) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    public ArrayList<Player> getStarting(LineupFormDTO lineupFormDTO) {

        ArrayList<PlayerDTO> startingPlayers = lineupFormDTO.getStartingPlayers();

        ArrayList<Player> starting = (ArrayList<Player>) startingPlayers.stream()
                .map(p -> new Player(teamMemberRepository.findByTeamMemberId(p.getTeamMember().getTeamMemberId()), p.getPosition(), p.getOrderNum()))
                .collect(Collectors.toList());

        log.info("[getStarting] starting={}", starting);

        return starting;
    }

    // 필요가 없네...?
//    public ArrayList<PlayerDTO> getStarting2(LineupFormDTO lineupFormDTO) {
//
//        ArrayList<PlayerDTO> startingPlayers = lineupFormDTO.getStartingPlayers();
//
//        ArrayList<PlayerDTO> starting = (ArrayList<PlayerDTO>) startingPlayers.stream()
//                .map(p -> PlayerDTO.from(p))
//                .collect(Collectors.toList());
//
//        log.info("[getStarting] starting={}", starting);
//
//        return starting;
//    }

    public ArrayList<Player> getBench(LineupFormDTO lineupFormDto) {

        ArrayList<PlayerDTO> benchPlayers = lineupFormDto.getBenchPlayers();

        ArrayList<Player> bench = (ArrayList<Player>) benchPlayers.stream()
                .map(p -> new Player(teamMemberRepository.findByTeamMemberId(p.getTeamMember().getTeamMemberId()), p.getPosition(), p.getOrderNum()))
                .collect(Collectors.toList());

        log.info("[getBench] bench={}", bench);

        return bench;
    }
}
