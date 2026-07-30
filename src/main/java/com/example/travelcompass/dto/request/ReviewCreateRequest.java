package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 여행 후기(리뷰) 작성 요청 시 클라이언트로부터 전달받는 데이터를 담는 DTO이다.
 *
 * <p>컨트롤러 파라미터에 {@code @Valid}가 함께 붙어야 아래 검증 애노테이션들이 동작한다.
 * 검증 실패 시 GlobalExceptionHandler가 필드별 메시지를 모아 응답해준다.</p>
 */
@Getter
@Setter
public class ReviewCreateRequest {

    // 후기를 작성할 대상 국가 코드. @NotBlank로 필수 입력 강제
    @NotBlank(message = "국가 코드를 입력해주세요.")
    private String countryCode;

    // 평점(1~5점). @Min/@Max로 허용 범위를 벗어나면 검증 실패 처리
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    private int rating;

    // 후기 본문 내용. @NotBlank로 빈 값 입력 방지
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

}
