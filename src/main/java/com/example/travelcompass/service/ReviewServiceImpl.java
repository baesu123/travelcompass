package com.example.travelcompass.service;

import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.CommentCreateRequest;
import com.example.travelcompass.dto.request.ReviewCreateRequest;
import com.example.travelcompass.dto.response.CommentResponse;
import com.example.travelcompass.dto.response.ReviewResponse;
import com.example.travelcompass.entity.Comment;
import com.example.travelcompass.entity.Member;
import com.example.travelcompass.entity.Review;
import com.example.travelcompass.mapper.CommentMapper;
import com.example.travelcompass.mapper.MemberMapper;
import com.example.travelcompass.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReviewService의 구현체. 리뷰(Review)와 댓글(Comment)에 대한 CRUD를 MyBatis Mapper로 처리하고,
 * 작성자의 닉네임을 붙이거나(MemberMapper) 국가의 한글 이름을 채우는(RestCountriesClient) 등
 * 여러 Mapper/외부 클라이언트를 조합해 화면에 필요한 응답 DTO를 완성하는 역할을 한다.
 */
@Service
@RequiredArgsConstructor // final 필드 4개를 생성자 주입받는다. (Mapper 3개 + 외부 API 클라이언트 1개)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final CommentMapper commentMapper;
    private final MemberMapper memberMapper; // 리뷰/댓글 작성자의 닉네임을 조회하기 위해 사용
    private final RestCountriesClient restCountriesClient; // 국가 코드 -> 한글 국가명 조회용 외부 API 클라이언트

    /**
     * 특정 국가의 리뷰 목록을 조회한다. 목록 화면이므로 댓글은 함께 조회하지 않아 성능을 아낀다.
     * @param countryCode 조회할 국가 코드
     * @return 리뷰 목록(댓글 미포함)
     */
    @Override
    public List<ReviewResponse> getReviews(String countryCode) {
        return reviewMapper.findAllByCountryCode(countryCode.toUpperCase()).stream()
                .map(review -> toReviewResponse(review, false)) // includeComments=false: 댓글은 조회하지 않음
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 하나의 상세 정보를 조회한다. 상세 화면이므로 댓글 목록까지 함께 채워서 반환한다.
     * @param reviewId 조회할 리뷰의 ID
     * @return 댓글이 포함된 리뷰 상세 정보
     */
    @Override
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return toReviewResponse(review, true); // includeComments=true: 댓글까지 함께 조회
    }

    /**
     * 새 리뷰를 작성한다.
     * @param memberId 작성자 회원의 ID
     * @param request 국가 코드, 평점, 본문 내용이 담긴 요청 DTO
     */
    @Override
    @Transactional
    public void createReview(Long memberId, ReviewCreateRequest request) {
        String countryCode = request.getCountryCode().toUpperCase();
        reviewMapper.insert(Review.builder()
                .memberId(memberId)
                .countryCode(countryCode)
                // 저장 시점에 외부 API로 한글 국가명을 조회해 함께 저장해두면, 조회할 때마다
                // 외부 API를 다시 호출하지 않고 DB에 저장된 값을 그대로 쓸 수 있다.
                .countryName(restCountriesClient.getKoreanName(countryCode))
                .rating(request.getRating())
                .content(request.getContent())
                .build());
    }

    /**
     * 기존 리뷰를 수정한다. id와 memberId를 함께 조건으로 걸어 작성자 본인만 수정 가능하도록 보호한다.
     * @param memberId 수정을 요청한 회원의 ID
     * @param reviewId 수정할 리뷰의 ID
     * @param request 수정할 국가 코드, 평점, 본문 내용이 담긴 요청 DTO
     */
    @Override
    @Transactional
    public void updateReview(Long memberId, Long reviewId, ReviewCreateRequest request) {
        String countryCode = request.getCountryCode().toUpperCase();
        // 수정할 내용을 담은 Review 엔티티를 새로 만들어 UPDATE 쿼리에 통째로 넘긴다.
        Review review = Review.builder()
                .id(reviewId)
                .memberId(memberId)
                .countryCode(countryCode)
                .countryName(restCountriesClient.getKoreanName(countryCode))
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        // UPDATE 쿼리가 id + memberId 조건이라 영향받은 행이 0건이면 본인 소유가 아니거나 없는 리뷰다.
        requireAffected(reviewMapper.updateByIdAndMemberId(review), ErrorCode.REVIEW_NOT_FOUND);
    }

    /**
     * 리뷰를 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID
     * @param reviewId 삭제할 리뷰의 ID
     */
    @Override
    @Transactional
    public void deleteReview(Long memberId, Long reviewId) {
        requireAffected(reviewMapper.deleteByIdAndMemberId(reviewId, memberId), ErrorCode.REVIEW_NOT_FOUND);
    }

    /**
     * 리뷰에 댓글을 작성한다. 존재하지 않는 리뷰에는 댓글을 달 수 없도록 먼저 리뷰 존재 여부를 검사한다.
     * @param memberId 댓글 작성자 회원의 ID
     * @param reviewId 댓글이 달릴 리뷰의 ID
     * @param request 댓글 내용이 담긴 요청 DTO
     */
    @Override
    @Transactional
    public void addComment(Long memberId, Long reviewId, CommentCreateRequest request) {
        if (reviewMapper.findById(reviewId) == null) {
            // 삭제되었거나 존재하지 않는 리뷰에 댓글이 달리는 것을 막는다.
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        commentMapper.insert(Comment.builder()
                .reviewId(reviewId)
                .memberId(memberId)
                .content(request.getContent())
                .build());
    }

    /**
     * 댓글 내용을 수정한다. id와 memberId를 함께 조건으로 걸어 작성자 본인만 수정 가능하도록 보호한다.
     * @param memberId 수정을 요청한 회원의 ID
     * @param reviewId 댓글이 속한 리뷰의 ID (Mapper 쿼리에는 직접 쓰이지 않지만 인터페이스 시그니처 통일용)
     * @param commentId 수정할 댓글의 ID
     * @param request 수정할 댓글 내용이 담긴 요청 DTO
     */
    @Override
    @Transactional
    public void updateComment(Long memberId, Long reviewId, Long commentId, CommentCreateRequest request) {
        requireAffected(commentMapper.updateContentByIdAndMemberId(commentId, memberId, request.getContent()),
                ErrorCode.COMMENT_NOT_FOUND);
    }

    /**
     * 댓글을 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID
     * @param reviewId 댓글이 속한 리뷰의 ID (Mapper 쿼리에는 직접 쓰이지 않지만 인터페이스 시그니처 통일용)
     * @param commentId 삭제할 댓글의 ID
     */
    @Override
    @Transactional
    public void deleteComment(Long memberId, Long reviewId, Long commentId) {
        requireAffected(commentMapper.deleteByIdAndMemberId(commentId, memberId), ErrorCode.COMMENT_NOT_FOUND);
    }

    /**
     * 소유권 검증(id + memberId)이 걸린 update/delete 쿼리는 영향받은 행이 0건이면
     * "존재하지 않음"과 "내 소유가 아님"을 구분하지 않고 동일하게 NOT_FOUND로 응답한다.
     * (다른 회원의 데이터 존재 여부를 노출하지 않기 위한 의도적인 설계)
     */
    private void requireAffected(int affectedRows, ErrorCode errorCode) {
        if (affectedRows == 0) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * Review 엔티티를 화면 응답용 ReviewResponse DTO로 변환한다.
     * 작성자 닉네임은 Review에 저장되어 있지 않으므로 memberId로 Member를 다시 조회해서 채워 넣는다.
     * @param review 변환할 리뷰 엔티티
     * @param includeComments true면 댓글 목록까지 함께 조회해 채움(상세 조회용), false면 생략(목록 조회용, 성능 절약)
     * @return 화면에 내려줄 리뷰 응답 DTO
     */
    private ReviewResponse toReviewResponse(Review review, boolean includeComments) {
        // 탈퇴 등으로 작성자가 사라졌을 수도 있으므로 null 가능성을 감안해 author != null로 방어한다.
        Member author = memberMapper.findById(review.getMemberId());

        List<CommentResponse> comments = includeComments
                ? commentMapper.findAllByReviewId(review.getId()).stream()
                        .map(this::toCommentResponse)
                        .collect(Collectors.toList())
                : Collections.emptyList(); // 목록 조회 시에는 댓글을 굳이 조회하지 않아 불필요한 쿼리를 줄인다.

        return ReviewResponse.builder()
                .id(review.getId())
                .authorNickname(author != null ? author.getNickname() : null)
                .countryCode(review.getCountryCode())
                .countryName(review.getCountryName())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .comments(comments)
                .build();
    }

    /**
     * Comment 엔티티를 화면 응답용 CommentResponse DTO로 변환한다.
     * 리뷰 변환과 마찬가지로 작성자 닉네임을 채우기 위해 Member를 다시 조회한다.
     * @param comment 변환할 댓글 엔티티
     * @return 화면에 내려줄 댓글 응답 DTO
     */
    private CommentResponse toCommentResponse(Comment comment) {
        Member author = memberMapper.findById(comment.getMemberId());

        return CommentResponse.builder()
                .id(comment.getId())
                .authorNickname(author != null ? author.getNickname() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
