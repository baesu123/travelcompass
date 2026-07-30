package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private String authorNickname;
    private String countryCode;
    private String countryName;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;

}
