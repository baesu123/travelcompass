package com.example.travelcompass.service;

import com.example.travelcompass.dto.request.CommentCreateRequest;
import com.example.travelcompass.dto.request.ReviewCreateRequest;
import com.example.travelcompass.dto.response.ReviewResponse;

import java.util.List;

/**
 * 국가별 여행 리뷰와 그에 달린 댓글을 관리하는 서비스 인터페이스.
 * 리뷰(Review)와 댓글(Comment) CRUD를 모두 담당한다.
 */
public interface ReviewService {

    /**
     * 특정 국가의 리뷰 목록을 조회한다. (목록 조회이므로 댓글은 포함하지 않음)
     * @param countryCode 조회할 국가의 코드(ISO 등)
     * @return 해당 국가의 리뷰 목록(ReviewResponse)
     */
    List<ReviewResponse> getReviews(String countryCode);

    /**
     * 리뷰 상세 정보를 조회한다. (상세 조회이므로 댓글 목록도 함께 포함)
     * @param reviewId 조회할 리뷰의 ID
     * @return 리뷰 상세 정보(댓글 포함)
     */
    ReviewResponse getReview(Long reviewId);

    /**
     * 새 리뷰를 작성한다.
     * @param memberId 작성자 회원의 ID
     * @param request 리뷰 내용(국가 코드, 평점, 본문 등)이 담긴 요청 DTO
     */
    void createReview(Long memberId, ReviewCreateRequest request);

    /**
     * 기존 리뷰를 수정한다.
     * @param memberId 수정을 요청한 회원의 ID(작성자 본인인지 검증용)
     * @param reviewId 수정할 리뷰의 ID
     * @param request 수정할 리뷰 내용이 담긴 요청 DTO
     */
    void updateReview(Long memberId, Long reviewId, ReviewCreateRequest request);

    /**
     * 리뷰를 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID(작성자 본인인지 검증용)
     * @param reviewId 삭제할 리뷰의 ID
     */
    void deleteReview(Long memberId, Long reviewId);

    /**
     * 리뷰에 댓글을 작성한다.
     * @param memberId 댓글 작성자 회원의 ID
     * @param reviewId 댓글이 달릴 리뷰의 ID
     * @param request 댓글 내용이 담긴 요청 DTO
     */
    void addComment(Long memberId, Long reviewId, CommentCreateRequest request);

    /**
     * 댓글 내용을 수정한다.
     * @param memberId 수정을 요청한 회원의 ID(작성자 본인인지 검증용)
     * @param reviewId 댓글이 속한 리뷰의 ID
     * @param commentId 수정할 댓글의 ID
     * @param request 수정할 댓글 내용이 담긴 요청 DTO
     */
    void updateComment(Long memberId, Long reviewId, Long commentId, CommentCreateRequest request);

    /**
     * 댓글을 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID(작성자 본인인지 검증용)
     * @param reviewId 댓글이 속한 리뷰의 ID
     * @param commentId 삭제할 댓글의 ID
     */
    void deleteComment(Long memberId, Long reviewId, Long commentId);

}
