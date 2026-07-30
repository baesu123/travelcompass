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

/**
 * 여행지 리뷰 및 리뷰에 달린 댓글(comment)을 다루는 REST API 컨트롤러.
 * 리뷰 조회는 비로그인 사용자도 가능하지만, 작성/수정/삭제는 로그인이 필요한 기능이다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    // 리뷰/댓글 관련 비즈니스 로직(작성 권한 검증, DB 처리 등)을 담당하는 서비스.
    private final ReviewService reviewService;

    /**
     * 특정 국가에 대한 리뷰 목록을 조회한다.
     *
     * @param countryCode 조회할 국가 코드
     * @return 해당 국가의 리뷰 목록을 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping
    public ApiResponse<List<ReviewResponse>> getReviews(@RequestParam String countryCode) {
        return ApiResponse.success(reviewService.getReviews(countryCode));
    }

    /**
     * 리뷰 하나의 상세 정보를 조회한다.
     *
     * @param reviewId 조회할 리뷰의 식별자(PK)
     * @return 리뷰 상세 정보를 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ApiResponse.success(reviewService.getReview(reviewId));
    }

    /**
     * 새로운 리뷰를 작성한다. 로그인한 회원만 가능하다.
     *
     * @param memberDetails 로그인한 회원 정보 (작성자로 기록됨)
     * @param request       작성할 리뷰 내용을 담은 요청 DTO
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @PostMapping
    public ApiResponse<Void> createReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @Valid @RequestBody ReviewCreateRequest request) {
        reviewService.createReview(memberDetails.getMemberId(), request);
        return ApiResponse.success(null);
    }

    /**
     * 기존 리뷰의 내용을 전체 수정한다. 작성자 본인만 가능하도록 서비스에서 검증한다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param reviewId      수정할 리뷰의 식별자(PK)
     * @param request       수정할 내용을 담은 요청 DTO
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @PutMapping("/{reviewId}")
    public ApiResponse<Void> updateReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable Long reviewId,
                                           @Valid @RequestBody ReviewCreateRequest request) {
        // PUT은 리소스 전체를 갈아끼우는(교체) 의미로 사용하는 HTTP 메서드이다.
        reviewService.updateReview(memberDetails.getMemberId(), reviewId, request);
        return ApiResponse.success(null);
    }

    /**
     * 리뷰를 삭제한다. 작성자 본인만 가능하도록 서비스에서 검증한다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param reviewId      삭제할 리뷰의 식별자(PK)
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable Long reviewId) {
        reviewService.deleteReview(memberDetails.getMemberId(), reviewId);
        return ApiResponse.success(null);
    }

    /**
     * 특정 리뷰에 댓글을 작성한다.
     *
     * @param memberDetails 로그인한 회원 정보 (댓글 작성자)
     * @param reviewId      댓글을 달 대상 리뷰의 식별자(PK)
     * @param request       작성할 댓글 내용을 담은 요청 DTO
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @PostMapping("/{reviewId}/comments")
    public ApiResponse<Void> addComment(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable Long reviewId,
                                         @Valid @RequestBody CommentCreateRequest request) {
        reviewService.addComment(memberDetails.getMemberId(), reviewId, request);
        return ApiResponse.success(null);
    }

    /**
     * 댓글 내용을 수정한다. 작성자 본인만 가능하도록 서비스에서 검증한다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param reviewId      댓글이 속한 리뷰의 식별자(PK)
     * @param commentId     수정할 댓글의 식별자(PK)
     * @param request       수정할 댓글 내용을 담은 요청 DTO
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @PutMapping("/{reviewId}/comments/{commentId}")
    public ApiResponse<Void> updateComment(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @PathVariable Long reviewId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentCreateRequest request) {
        reviewService.updateComment(memberDetails.getMemberId(), reviewId, commentId, request);
        return ApiResponse.success(null);
    }

    /**
     * 댓글을 삭제한다. 작성자 본인만 가능하도록 서비스에서 검증한다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param reviewId      댓글이 속한 리뷰의 식별자(PK)
     * @param commentId     삭제할 댓글의 식별자(PK)
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @PathVariable Long reviewId,
                                            @PathVariable Long commentId) {
        reviewService.deleteComment(memberDetails.getMemberId(), reviewId, commentId);
        return ApiResponse.success(null);
    }

}
