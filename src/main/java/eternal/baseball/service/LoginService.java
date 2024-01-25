package eternal.baseball.service;

import eternal.baseball.domain.Member;
import eternal.baseball.dto.login.LoginDTO.*;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.dto.util.ResponseDataDTO;
import eternal.baseball.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    public final MemberRepository memberRepository;

    /**
     * 로그인
     */
    public ResponseDataDTO<MemberDTO> login(LoginRequestDTO loginRequest) {
        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        Member findMemberDTO = memberRepository.findByLoginId(loginRequest.getLoginId())
                .filter(m -> m.getPassword().equals(loginRequest.getPassword()))
                .orElse(null);

        if (findMemberDTO == null) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorCode("loginFail")
                    .errorMessage("아이디 또는 비밀번호가 맞지 않습니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }

        if (!bindingErrors.isEmpty()) {
            return ResponseDataDTO.<MemberDTO>builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        eternal.baseball.dto.member.MemberDTO memberDTO = eternal.baseball.dto.member.MemberDTO.builder()
                .memberId(findMemberDTO.getMemberId())
                .loginId(findMemberDTO.getLoginId())
                .name(findMemberDTO.getName())
                .birthday(findMemberDTO.getBirthday())
                .build();

        return ResponseDataDTO.<MemberDTO>builder()
                .error(false)
                .data(memberDTO)
                .build();
    }
}
