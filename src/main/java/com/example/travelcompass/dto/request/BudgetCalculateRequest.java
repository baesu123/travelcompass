package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 여행 예산 계산기 요청 시 클라이언트로부터 전달받는 데이터를 담는 DTO이다.
 *
 * <p>여행 일수와 1박당 숙박비(원화 기준)를 바탕으로 총 예산을 계산하고,
 * 지정한 현지 통화로 환산해주는 기능에서 사용되는 입력값들이다.</p>
 */
@Getter
@Setter
public class BudgetCalculateRequest {

    // 여행 일수. @Min(1): 0일이나 음수 입력을 방지 (최소 1일 이상 여행해야 함)
    @Min(value = 1, message = "여행 일수는 1일 이상이어야 합니다.")
    private int days;

    // 1박당 숙박비(원화, KRW 기준). @Min(0): 음수 금액 입력 방지
    @Min(value = 0, message = "숙박비는 0원 이상이어야 합니다.")
    private long costPerNightKrw;

    // 환산하고자 하는 현지 통화 코드(예: "USD", "JPY"). @NotBlank로 필수 입력 강제
    @NotBlank(message = "현지 통화 코드를 입력해주세요.")
    private String targetCurrency;

}
