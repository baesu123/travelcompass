package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 새로운 체크리스트 항목(예: "여권 챙기기")을 추가할 때 클라이언트로부터 전달받는 데이터를 담는 DTO이다.
 */
@Getter
@Setter
public class ChecklistCreateRequest {

    // 새로 추가할 체크리스트 항목의 이름. @NotBlank로 빈 값 등록 방지
    @NotBlank(message = "체크리스트 항목명을 입력해주세요.")
    private String itemName;

}
