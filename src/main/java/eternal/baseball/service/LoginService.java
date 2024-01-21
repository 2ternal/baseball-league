package eternal.baseball.service;

import eternal.baseball.domain.Member;
import eternal.baseball.dto.login.LoginDTO.*;
import eternal.baseball.dto.member.MemberDTO;
import eternal.baseball.dto.member.ResponseMemberDTO;
import eternal.baseball.dto.util.BindingErrorDTO;
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
    public ResponseMemberDTO login(LoginRequestDTO loginRequest) {
        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        Member findMember = memberRepository.findByLoginId(loginRequest.getLoginId())
                .filter(m -> m.getPassword().equals(loginRequest.getPassword()))
                .orElse(null);

        if (findMember == null) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorCode("loginFail")
                    .errorMessage("아이디 또는 비밀번호가 맞지 않습니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }

        if (!bindingErrors.isEmpty()) {
            return ResponseMemberDTO.builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        MemberDTO memberDTO = MemberDTO.builder()
                .memberId(findMember.getMemberId())
                .loginId(findMember.getLoginId())
                .name(findMember.getName())
                .birthday(findMember.getBirthday())
                .build();

        return ResponseMemberDTO.builder()
                .error(false)
                .member(memberDTO)
                .build();
    }
}
