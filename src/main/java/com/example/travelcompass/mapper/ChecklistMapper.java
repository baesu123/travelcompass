package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Checklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChecklistMapper {

    List<Checklist> findAllByMemberId(Long memberId);

    void insert(Checklist checklist);

    int updateCheckedByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId, @Param("checked") boolean checked);

    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
