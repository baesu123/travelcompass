package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChecklistCreateRequest {

    @NotBlank(message = "체크리스트 항목명을 입력해주세요.")
    private String itemName;

}
