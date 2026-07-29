package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    Member findByUsername(String username);

    int countByUsername(String username);

    void insert(Member member);

}
