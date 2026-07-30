package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 회원(member) 정보를 다루는 Mapper 인터페이스.
 * 로그인/회원가입 시 필요한 조회, 중복 확인, 등록 기능을 담당한다.
 *
 * <p>초보자 팁: 이 인터페이스는 SQL을 직접 갖고 있지 않고, {@code @Mapper} 애노테이션만 붙어 있다.
 * MyBatis가 같은 이름의 {@code MemberMapper.xml}을 찾아 메서드 이름과 일치하는 SQL을 실행해 준다.</p>
 */
@Mapper
public interface MemberMapper {

    /**
     * 로그인 아이디(username)로 회원 정보를 조회한다.
     * 주로 로그인 인증 시 비밀번호 검증을 위해 사용된다.
     *
     * @param username 조회할 회원의 로그인 아이디
     * @return 일치하는 회원 정보 (없으면 null)
     */
    Member findByUsername(String username);

    /**
     * 회원 고유 ID(PK)로 회원 정보를 조회한다.
     * 주로 세션에 저장된 회원 ID를 바탕으로 회원 정보를 다시 불러올 때 사용된다.
     *
     * @param id 조회할 회원의 고유 ID
     * @return 일치하는 회원 정보 (없으면 null)
     */
    Member findById(Long id);

    /**
     * 특정 로그인 아이디(username)가 이미 사용 중인지 개수로 확인한다.
     * 회원가입 시 아이디 중복 검사에 사용된다.
     *
     * @param username 중복 확인할 로그인 아이디
     * @return 일치하는 회원 수 (0이면 사용 가능, 1 이상이면 이미 사용 중)
     */
    int countByUsername(String username);

    /**
     * 새로운 회원 정보를 DB에 저장(INSERT)한다.
     *
     * @param member 저장할 회원 정보(아이디, 비밀번호, 닉네임 등)를 담은 엔티티 객체
     */
    void insert(Member member);

}
