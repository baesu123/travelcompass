package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> findAllByReviewId(Long reviewId);

    void insert(Comment comment);

    int updateContentByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId, @Param("content") String content);

    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
