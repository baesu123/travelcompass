package com.example.travelcompass.service;

import com.example.travelcompass.dto.request.CommentCreateRequest;
import com.example.travelcompass.dto.request.ReviewCreateRequest;
import com.example.travelcompass.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    List<ReviewResponse> getReviews(String countryCode);

    ReviewResponse getReview(Long reviewId);

    void createReview(Long memberId, ReviewCreateRequest request);

    void updateReview(Long memberId, Long reviewId, ReviewCreateRequest request);

    void deleteReview(Long memberId, Long reviewId);

    void addComment(Long memberId, Long reviewId, CommentCreateRequest request);

    void deleteComment(Long memberId, Long reviewId, Long commentId);

}
