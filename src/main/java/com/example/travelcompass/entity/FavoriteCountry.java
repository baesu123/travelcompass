package com.example.travelcompass.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteCountry {

    private Long id;
    private Long memberId;
    private String countryCode;
    private String countryName;
    private LocalDateTime createdAt;

}
