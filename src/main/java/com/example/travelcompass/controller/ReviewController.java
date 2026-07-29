package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.config.MemberDetails;
import com.example.travelcompass.dto.request.CommentCreateRequest;
import com.example.travelcompass.dto.request.ReviewCreateRequest;
import com.example.travelcompass.dto.response.ReviewResponse;
import com.example.travelcompass.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ApiResponse<List<ReviewResponse>> getReviews(@RequestParam String countryCode) {
        return ApiResponse.success(reviewService.getReviews(countryCode));
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ApiResponse.success(reviewService.getReview(reviewId));
    }

    @PostMapping
    public ApiResponse<Void> createReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @Valid @RequestBody ReviewCreateRequest request) {
        reviewService.createReview(memberDetails.getMemberId(), request);
        return ApiResponse.success(null);
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<Void> updateReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable Long reviewId,
                                           @Valid @RequestBody ReviewCreateRequest request) {
        reviewService.updateReview(memberDetails.getMemberId(), reviewId, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable Long reviewId) {
        reviewService.deleteReview(memberDetails.getMemberId(), reviewId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{reviewId}/comments")
    public ApiResponse<Void> addComment(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable Long reviewId,
                                         @Valid @RequestBody CommentCreateRequest request) {
        reviewService.addComment(memberDetails.getMemberId(), reviewId, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/{reviewId}/comments/{commentId}")
    public ApiResponse<Void> updateComment(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @PathVariable Long reviewId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentCreateRequest request) {
        reviewService.updateComment(memberDetails.getMemberId(), reviewId, commentId, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @PathVariable Long reviewId,
                                            @PathVariable Long commentId) {
        reviewService.deleteComment(memberDetails.getMemberId(), reviewId, commentId);
        return ApiResponse.success(null);
    }

}
