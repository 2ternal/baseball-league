package eternal.baseball.service;

import eternal.baseball.domain.Lineup;
import eternal.baseball.domain.TeamMember;
import eternal.baseball.domain.custom.Player;
import eternal.baseball.domain.custom.Position;
import eternal.baseball.dto.lineup.LineupChangeCard;
import eternal.baseball.dto.lineup.LineupChangeDTO;
import eternal.baseball.dto.lineup.LineupDTO;
import eternal.baseball.dto.lineup.LineupFormDTO;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.player.PlayerDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.repository.LineupRepository;
import eternal.baseball.repository.TeamMemberRepository;
import eternal.baseball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineupService {

    private final TeamMemberRepository teamMemberRepository;
    private final LineupRepository lineupRepository;
    private final TeamRepository teamRepository;

    /**
     * 새 라인업 추가
     */
    public LineupDTO createLineup(LineupFormDTO lineupForm, MemberDTO loginMember) {
        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);

        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamCode(loginMember.getMemberId(), lineupForm.getTeamCode());

        Lineup lineup = Lineup.builder()
                .team(teamRepository.findByTeamCode(lineupForm.getTeamCode()))
                .writer(loginTeamMember)
                .lineupName(lineupForm.getLineupName())
                .starting(starting)
                .bench(bench)
                .updateTime(new Date())
                .build();

        lineupRepository.save(lineup);

        return LineupDTO.from(lineup);
    }

    /**
     * 라인업 정보 수정
     */
    public LineupDTO editLineup(Long lineupId, LineupFormDTO lineupForm, MemberDTO loginMember) {
        ArrayList<Player> starting = getStarting(lineupForm);
        ArrayList<Player> bench = getBench(lineupForm);

        TeamMember loginTeamMember = teamMemberRepository.findByMemberIdTeamCode(loginMember.getMemberId(), lineupForm.getTeamCode());

        Lineup lineup = Lineup.builder()
                .team(teamRepository.findByTeamCode(lineupForm.getTeamCode()))
                .writer(loginTeamMember)
                .lineupName(lineupForm.getLineupName())
                .starting(starting)
                .bench(bench)
                .updateTime(new Date())
                .build();

        lineupRepository.edit(lineupId, lineup);

        return LineupDTO.from(lineup);
    }

    /**
     * 라인업 아이디로 라인업 삭제
     */
    public LineupDTO deleteLineup(Long lineupId) {
        return LineupDTO.from(lineupRepository.deleteLineup(lineupId));
    }

    /**
     * 라인업 찾기
     */
    public LineupDTO findLineup(Long lineupId) {
        return LineupDTO.from(lineupRepository.findByLineupId(lineupId));
    }

    /**
     * teamId 로 라인업 리스트 찾기
     */
    public List<LineupDTO> findTeamLineupList(String teamCode) {
        return lineupRepository.findAll().stream()
                .filter(l -> l.getTeam().getTeamCode().equals(teamCode))
                .map(LineupDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 라인업 작성 페이지 타순 교체
     */
    public ResponseDataDTO<LineupChangeDTO> changeOrder(LineupChangeCard lineupChangeCard, LineupFormDTO lineupForm) {

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
            return ResponseDataDTO.<LineupChangeDTO>builder()
                    .data(LineupChangeDTO.builder()
                            .status(false)
                            .errCode("needStarting")
                            .errMessage("주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다")
                            .lineupForm(lineupForm)
                            .build())
                    .build();
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
                return ResponseDataDTO.<LineupChangeDTO>builder()
                        .data(LineupChangeDTO.builder()
                                .status(false)
                                .errCode("needTwoPlayers")
                                .errMessage("2명의 선수를 선택해야 합니다")
                                .lineupForm(lineupForm)
                                .build())
                        .build();
            }
            player2OrderIndex = lineupChangeCard.getBench() - 1;
            player2 = benchPlayers.get(player2OrderIndex - 9);
        }

        Position player1Position = player1.getPosition();
        Integer player1OrderNum = player1.getOrderNum();

        Position player2Position = player2.getPosition();
        Integer player2OrderNum = player2.getOrderNum();

        player2.setPosition(player1Position);
        player2.setOrderNum(player1OrderNum);

        player1.setPosition(player2Position);
        player1.setOrderNum(player2OrderNum);

        startingPlayers.set(player1OrderIndex, player2);
        if (player2OrderIndex < 9) {
            startingPlayers.set(player2OrderIndex, player1);
        } else {
            benchPlayers.set(player2OrderIndex - 9, player1);
        }

        lineupForm.setStartingPlayers(startingPlayers);
        lineupForm.setBenchPlayers(benchPlayers);

        return ResponseDataDTO.<LineupChangeDTO>builder()
                .data(LineupChangeDTO.builder()
                        .status(true)
                        .lineupForm(lineupForm)
                        .build())
                .build();
    }

    /**
     * 라인업 작성 페이지 포지션 교체
     */
    public ResponseDataDTO<LineupChangeDTO> changePosition(LineupChangeCard lineupChangeCard, LineupFormDTO lineupForm) {

        List<Boolean> trueList = lineupChangeCard.isTrueList();
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
            return ResponseDataDTO.<LineupChangeDTO>builder()
                    .data(LineupChangeDTO.builder()
                            .status(false)
                            .errCode("needStarting")
                            .errMessage("주전 라인업에서 최소한 1명의 교체 선수를 골라야 합니다")
                            .lineupForm(lineupForm)
                            .build())
                    .build();
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
            return ResponseDataDTO.<LineupChangeDTO>builder()
                    .data(LineupChangeDTO.builder()
                            .status(false)
                            .errCode("needTwoStartingPlayers")
                            .errMessage("주전 라인업에서 2명의 선수를 선택해야 합니다")
                            .lineupForm(lineupForm)
                            .build())
                    .build();
        }

        Position player1Position = player1.getPosition();
        Position player2Position = player2.getPosition();

        player1.setPosition(player2Position);
        player2.setPosition(player1Position);

        //교체
        startingPlayers.set(player1OrderIndex, player1);
        startingPlayers.set(player2OrderIndex, player2);

        lineupForm.setStartingPlayers(startingPlayers);

        return ResponseDataDTO.<LineupChangeDTO>builder()
                .data(LineupChangeDTO.builder()
                        .status(true)
                        .lineupForm(lineupForm)
                        .build())
                .build();
    }

    private ArrayList<Player> getStarting(LineupFormDTO lineupFormDTO) {

        ArrayList<PlayerDTO> startingPlayers = lineupFormDTO.getStartingPlayers();

        ArrayList<Player> starting = (ArrayList<Player>) startingPlayers.stream()
                .map(p -> Player.builder().teamMember(teamMemberRepository.findByTeamMemberId(p.getTeamMember().getTeamMemberId()))
                        .position(p.getPosition())
                        .orderNum(p.getOrderNum())
                        .build())
                .collect(Collectors.toList());

        log.info("[getStarting] starting={}", starting);

        return starting;
    }

    private ArrayList<Player> getBench(LineupFormDTO lineupFormDto) {

        ArrayList<PlayerDTO> benchPlayers = lineupFormDto.getBenchPlayers();

        ArrayList<Player> bench = (ArrayList<Player>) benchPlayers.stream()
                .map(p -> Player.builder()
                        .teamMember(teamMemberRepository.findByTeamMemberId(p.getTeamMember().getTeamMemberId()))
                        .position(p.getPosition())
                        .orderNum(p.getOrderNum())
                        .build())
                .collect(Collectors.toList());

        log.info("[getBench] bench={}", bench);

        return bench;
    }
}
