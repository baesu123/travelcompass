package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 시 클라이언트로부터 전달받는 데이터를 담는 DTO이다.
 *
 * <p>컨트롤러에서 {@code @Valid}와 함께 바인딩되며, 아이디/비밀번호/닉네임 각각에 대한 형식 검증이
 * 컨트롤러 로직 실행 전에 자동으로 이루어진다. 아이디 중복 여부처럼 DB 조회가 필요한 검증은
 * 이 DTO 단계에서는 할 수 없으므로, 서비스 계층에서 별도로 확인 후
 * {@link com.example.travelcompass.common.exception.BusinessException}
 * (ErrorCode.DUPLICATE_USERNAME)을 던지는 방식으로 처리된다.</p>
 */
@Getter
@Setter
public class MemberJoinRequest {

    // 로그인에 사용할 아이디. @NotBlank(필수 입력) + @Size(4~20자)로 형식을 제한
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 입력해주세요.")
    private String username;

    // 로그인 비밀번호(평문으로 받아 서비스 계층에서 암호화 처리됨). @NotBlank + @Size(4~50자)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, max = 50, message = "비밀번호는 4자 이상 입력해주세요.")
    private String password;

    // 화면에 표시될 닉네임. @NotBlank로 필수 입력 강제
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

}
