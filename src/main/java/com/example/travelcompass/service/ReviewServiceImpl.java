package com.example.travelcompass.service;

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

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final CommentMapper commentMapper;
    private final MemberMapper memberMapper;

    @Override
    public List<ReviewResponse> getReviews(String countryCode) {
        return reviewMapper.findAllByCountryCode(countryCode.toUpperCase()).stream()
                .map(review -> toReviewResponse(review, false))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return toReviewResponse(review, true);
    }

    @Override
    @Transactional
    public void createReview(Long memberId, ReviewCreateRequest request) {
        reviewMapper.insert(Review.builder()
                .memberId(memberId)
                .countryCode(request.getCountryCode().toUpperCase())
                .rating(request.getRating())
                .content(request.getContent())
                .build());
    }

    @Override
    @Transactional
    public void updateReview(Long memberId, Long reviewId, ReviewCreateRequest request) {
        Review review = Review.builder()
                .id(reviewId)
                .memberId(memberId)
                .countryCode(request.getCountryCode().toUpperCase())
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        int updated = reviewMapper.update(review);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteReview(Long memberId, Long reviewId) {
        int deleted = reviewMapper.deleteByIdAndMemberId(reviewId, memberId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void addComment(Long memberId, Long reviewId, CommentCreateRequest request) {
        if (reviewMapper.findById(reviewId) == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        commentMapper.insert(Comment.builder()
                .reviewId(reviewId)
                .memberId(memberId)
                .content(request.getContent())
                .build());
    }

    @Override
    @Transactional
    public void updateComment(Long memberId, Long reviewId, Long commentId, CommentCreateRequest request) {
        int updated = commentMapper.updateContentByIdAndMemberId(commentId, memberId, request.getContent());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long memberId, Long reviewId, Long commentId) {
        int deleted = commentMapper.deleteByIdAndMemberId(commentId, memberId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    private ReviewResponse toReviewResponse(Review review, boolean includeComments) {
        Member author = memberMapper.findById(review.getMemberId());

        List<CommentResponse> comments = includeComments
                ? commentMapper.findAllByReviewId(review.getId()).stream()
                        .map(this::toCommentResponse)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return ReviewResponse.builder()
                .id(review.getId())
                .authorNickname(author != null ? author.getNickname() : null)
                .countryCode(review.getCountryCode())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .comments(comments)
                .build();
    }

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
