package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.CommentCreateRequest;
import com.example.travelcompass.dto.request.ReviewCreateRequest;
import com.example.travelcompass.dto.response.ReviewResponse;
import com.example.travelcompass.entity.Comment;
import com.example.travelcompass.entity.Member;
import com.example.travelcompass.entity.Review;
import com.example.travelcompass.mapper.CommentMapper;
import com.example.travelcompass.mapper.MemberMapper;
import com.example.travelcompass.mapper.ReviewMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewServiceImplTest {

    private final ReviewMapper reviewMapper = mock(ReviewMapper.class);
    private final CommentMapper commentMapper = mock(CommentMapper.class);
    private final MemberMapper memberMapper = mock(MemberMapper.class);
    private final ReviewServiceImpl reviewService = new ReviewServiceImpl(reviewMapper, commentMapper, memberMapper);

    @Test
    void 국가별_후기_목록은_댓글없이_조회된다() {
        Review review = Review.builder().id(1L).memberId(10L).countryCode("JP").rating(5).content("좋아요").build();
        when(reviewMapper.findAllByCountryCode("JP")).thenReturn(List.of(review));
        when(memberMapper.findById(10L)).thenReturn(Member.builder().id(10L).nickname("여행자").build());

        List<ReviewResponse> responses = reviewService.getReviews("jp");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAuthorNickname()).isEqualTo("여행자");
        assertThat(responses.get(0).getComments()).isEmpty();
        verify(commentMapper, times(0)).findAllByReviewId(any());
    }

    @Test
    void 후기_상세조회는_댓글도_함께_조회된다() {
        Review review = Review.builder().id(1L).memberId(10L).countryCode("JP").rating(5).content("좋아요").build();
        Comment comment = Comment.builder().id(100L).reviewId(1L).memberId(20L).content("저도 가고싶어요").build();
        when(reviewMapper.findById(1L)).thenReturn(review);
        when(commentMapper.findAllByReviewId(1L)).thenReturn(List.of(comment));
        when(memberMapper.findById(10L)).thenReturn(Member.builder().id(10L).nickname("여행자").build());
        when(memberMapper.findById(20L)).thenReturn(Member.builder().id(20L).nickname("댓글러").build());

        ReviewResponse response = reviewService.getReview(1L);

        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getComments().get(0).getAuthorNickname()).isEqualTo("댓글러");
    }

    @Test
    void 존재하지_않는_후기_조회는_예외가_발생한다() {
        when(reviewMapper.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> reviewService.getReview(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
    }

    @Test
    void 본인_소유가_아닌_후기_수정_삭제는_예외가_발생한다() {
        when(reviewMapper.updateByIdAndMemberId(any(Review.class))).thenReturn(0);
        when(reviewMapper.deleteByIdAndMemberId(1L, 10L)).thenReturn(0);

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setCountryCode("JP");
        request.setRating(5);
        request.setContent("수정");

        assertThatThrownBy(() -> reviewService.updateReview(10L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REVIEW_NOT_FOUND);

        assertThatThrownBy(() -> reviewService.deleteReview(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_후기에_댓글을_달_수_없다() {
        when(reviewMapper.findById(1L)).thenReturn(null);

        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("댓글");

        assertThatThrownBy(() -> reviewService.addComment(10L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
    }

    @Test
    void 본인_소유가_아닌_댓글_수정_삭제는_예외가_발생한다() {
        when(commentMapper.updateContentByIdAndMemberId(100L, 10L, "수정")).thenReturn(0);
        when(commentMapper.deleteByIdAndMemberId(100L, 10L)).thenReturn(0);

        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("수정");

        assertThatThrownBy(() -> reviewService.updateComment(10L, 1L, 100L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND);

        assertThatThrownBy(() -> reviewService.deleteComment(10L, 1L, 100L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
    }

}
