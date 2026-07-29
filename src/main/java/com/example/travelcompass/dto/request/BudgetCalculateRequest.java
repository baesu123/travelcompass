package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetCalculateRequest {

    @Min(value = 1, message = "여행 일수는 1일 이상이어야 합니다.")
    private int days;

    @Min(value = 0, message = "숙박비는 0원 이상이어야 합니다.")
    private long costPerNightKrw;

    @NotBlank(message = "현지 통화 코드를 입력해주세요.")
    private String targetCurrency;

}
