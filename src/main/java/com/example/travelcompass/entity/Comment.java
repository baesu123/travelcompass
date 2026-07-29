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
public class Comment {

    private Long id;
    private Long reviewId;
    private Long memberId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
