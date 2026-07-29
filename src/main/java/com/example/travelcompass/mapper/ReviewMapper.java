package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {

    List<Review> findAllByCountryCode(String countryCode);

    Review findById(Long id);

    void insert(Review review);

    void update(Review review);

    void deleteById(Long id);

}
