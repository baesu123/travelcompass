package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CountrySearchResultResponse {

    private final String code;
    private final String nameKo;
    private final String nameEn;

}
