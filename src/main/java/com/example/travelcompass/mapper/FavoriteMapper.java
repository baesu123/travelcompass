package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.FavoriteCountry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    List<FavoriteCountry> findAllByMemberId(Long memberId);

    int countByMemberIdAndCountryCode(@Param("memberId") Long memberId, @Param("countryCode") String countryCode);

    void insert(FavoriteCountry favoriteCountry);

    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
