package eternal.baseball.service;

import eternal.baseball.domain.Member;
import eternal.baseball.dto.member.*;
import eternal.baseball.dto.util.BindingErrorDTO;
import eternal.baseball.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    public final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    public ResponseMemberDTO signUp(SignUpMemberDTO signUpMember) {
        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        // 로그인 Id 중복 검증
        if (!ObjectUtils.isEmpty(memberRepository.findByLoginId(signUpMember.getLoginId()))) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("loginId")
                    .errorCode("duplicateId")
                    .errorMessage("중복되는 ID 입니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }
        // 이름 중복 검증
        if (!ObjectUtils.isEmpty(memberRepository.findByName(signUpMember.getName()))) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("name")
                    .errorCode("duplicateName")
                    .errorMessage("중복되는 회원 이름 입니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }
        // 비밀번호 확인 검증
        if (!Objects.equals(signUpMember.getPassword(), signUpMember.getPasswordCheck())) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("passwordCheck")
                    .errorCode("mismatchPassword")
                    .errorMessage("비밀번호가 일치하지 않습니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }

        if (!bindingErrors.isEmpty()) {
            return ResponseMemberDTO.builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        MemberDTO memberDTO = memberRepository.save(signUpMember.toEntity());
        return ResponseMemberDTO.builder()
                .error(false)
                .member(memberDTO)
                .build();
    }

    /**
     * 멤버 정보 수정
     */
    public ResponseMemberDTO editMember(MemberDTO sessionMember, EditMemberDTO editMember) {
        String memberPassword = memberRepository.findByMemberId(sessionMember.getMemberId())
                .getPassword();
        List<BindingErrorDTO> bindingErrors = new ArrayList<>();
        // 비밀번호 확인 검증
        if (!memberPassword.equals(editMember.getPassword())) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("password")
                    .errorCode("wrongPassword")
                    .errorMessage("비밀번호가 틀립니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }
        // 이름 중복 검증
        if (!Objects.equals(sessionMember.getName(), editMember.getName()) &&
                !ObjectUtils.isEmpty(memberRepository.findByName(editMember.getName()))) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("name")
                    .errorCode("duplicateName")
                    .errorMessage("중복되는 회원 이름 입니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }
        // 비밀번호 확인 검증
        if (!editMember.getChangePassword().equals(editMember.getChangePasswordCheck())) {
            BindingErrorDTO bindingErrorDTO = BindingErrorDTO.builder()
                    .errorField("changePasswordCheck")
                    .errorCode("wrongChangePassword")
                    .errorMessage("변경할 비밀번호가 일치하지 않습니다")
                    .build();

            bindingErrors.add(bindingErrorDTO);
        }

        if (!bindingErrors.isEmpty()) {
            return ResponseMemberDTO.builder()
                    .error(true)
                    .bindingErrors(bindingErrors)
                    .build();
        }

        MemberDTO memberDTO = memberRepository.edit(sessionMember.getMemberId(), editMember.toEntity());

        return ResponseMemberDTO.builder()
                .error(false)
                .member(memberDTO)
                .build();
    }

    /**
     * 멤버 아이디로 member 찾기
     */
    public MemberDTO findMember(Long MemberId) {
        Member member = memberRepository.findByMemberId(MemberId);
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthday(member.getBirthday())
                .build();
    }

    /**
     * 모든 member 찾기
     */
    public List<MemberDTO> findMembers() {
        return memberRepository.findAll().stream()
                .map(member -> MemberDTO.builder()
                        .memberId(member.getMemberId())
                        .loginId(member.getLoginId())
                        .name(member.getName())
                        .birthday(member.getBirthday())
                        .build())
                .collect(Collectors.toList());
    }
}
