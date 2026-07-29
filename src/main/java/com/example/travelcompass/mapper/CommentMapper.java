package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> findAllByReviewId(Long reviewId);

    void insert(Comment comment);

    void update(Comment comment);

    void deleteById(Long id);

    void deleteAllByReviewId(Long reviewId);

}
