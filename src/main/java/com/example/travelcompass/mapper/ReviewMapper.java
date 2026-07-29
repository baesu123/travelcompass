package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {

    List<Review> findAllByCountryCode(String countryCode);

    Review findById(Long id);

    void insert(Review review);

    int updateByIdAndMemberId(Review review);

    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
