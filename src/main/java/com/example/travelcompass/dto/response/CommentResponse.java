package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String authorNickname;
    private String content;
    private LocalDateTime createdAt;

}
