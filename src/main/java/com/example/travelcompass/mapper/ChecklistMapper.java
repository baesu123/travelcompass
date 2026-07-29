package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Checklist;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChecklistMapper {

    List<Checklist> findAllByMemberId(Long memberId);

    void insert(Checklist checklist);

    void updateChecked(Long id, boolean checked);

    void deleteById(Long id);

}
