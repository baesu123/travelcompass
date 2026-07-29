package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteCreateRequest {

    @NotBlank(message = "국가 코드를 입력해주세요.")
    private String countryCode;

}
