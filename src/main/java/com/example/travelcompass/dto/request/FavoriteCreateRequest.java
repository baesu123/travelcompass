package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 즐겨찾기(관심 국가) 등록 요청 시 클라이언트로부터 전달받는 데이터를 담는 DTO이다.
 *
 * <p>컨트롤러에서 {@code @Valid @RequestBody FavoriteCreateRequest}처럼 사용하면,
 * 스프링이 요청 JSON을 이 객체로 변환(바인딩)함과 동시에 아래 필드에 붙은 검증 애노테이션을
 * 자동으로 검사한다. 검증에 실패하면 컨트롤러 로직은 실행되지 않고
 * {@code MethodArgumentNotValidException}이 발생하여 전역 예외 처리기(GlobalExceptionHandler)가
 * 처리한다.</p>
 *
 * <p>{@literal @}Getter/@Setter(Lombok)로 각 필드의 getter/setter를 자동 생성해
 * 스프링이 요청 값을 채워 넣을 수 있게 한다.</p>
 */
@Getter
@Setter
public class FavoriteCreateRequest {

    // 즐겨찾기에 추가할 국가의 코드(예: "KR", "US"). @NotBlank: null이거나 빈 문자열/공백만 있으면 검증 실패
    @NotBlank(message = "국가 코드를 입력해주세요.")
    private String countryCode;

}
