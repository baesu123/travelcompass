package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FavoriteResponse {

    private Long id;
    private String countryCode;
    private String countryName;
    private LocalDateTime createdAt;

}
