package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.FavoriteCountry;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    List<FavoriteCountry> findAllByMemberId(Long memberId);

    int countByMemberIdAndCountryCode(Long memberId, String countryCode);

    void insert(FavoriteCountry favoriteCountry);

    void deleteById(Long id);

}
