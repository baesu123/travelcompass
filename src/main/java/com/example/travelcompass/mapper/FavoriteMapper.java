package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.FavoriteCountry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 회원이 즐겨찾기(관심 국가)로 등록한 국가 정보를 다루는 Mapper 인터페이스.
 * favorite_country(예시) 테이블에 대한 조회/등록/삭제 기능을 담당한다.
 *
 * <p>초보자 팁: 이 인터페이스에는 {@code @Mapper} 애노테이션만 있고 실제 SQL이 없다.
 * MyBatis는 이 인터페이스와 같은 이름/패키지 구조를 가진
 * {@code src/main/resources/mapper/FavoriteMapper.xml} 파일을 찾아서,
 * 여기 선언된 메서드 이름과 XML의 {@code id} 속성을 매칭시켜 실제 SQL을 실행한다.
 * 즉, 자바 코드에서는 "무엇을 할지"만 정의하고, "어떻게(SQL로) 할지"는 XML에 정의되어 있다.</p>
 */
@Mapper
public interface FavoriteMapper {

    /**
     * 특정 회원이 등록한 모든 즐겨찾기 국가 목록을 조회한다.
     *
     * @param memberId 조회할 회원의 고유 ID
     * @return 해당 회원이 즐겨찾기한 국가 목록 (없으면 빈 리스트)
     */
    List<FavoriteCountry> findAllByMemberId(Long memberId);

    /**
     * 특정 회원이 특정 국가를 이미 즐겨찾기에 등록했는지 개수로 확인한다.
     * 중복 등록 방지(이미 등록된 경우 다시 추가하지 못하게 막는 용도) 등에 사용된다.
     *
     * @param memberId    확인할 회원의 고유 ID
     * @param countryCode 확인할 국가 코드 (예: "KR", "JP")
     * @return 조건에 일치하는 row 개수 (0이면 미등록, 1 이상이면 이미 등록됨)
     */
    int countByMemberIdAndCountryCode(@Param("memberId") Long memberId, @Param("countryCode") String countryCode);

    /**
     * 새로운 즐겨찾기 국가 정보를 DB에 저장(INSERT)한다.
     *
     * @param favoriteCountry 저장할 즐겨찾기 정보(회원 ID, 국가 코드 등)를 담은 엔티티 객체
     */
    void insert(FavoriteCountry favoriteCountry);

    /**
     * 특정 회원이 등록한 즐겨찾기 항목을 ID로 삭제한다.
     * memberId 조건을 함께 걸어, 다른 회원의 즐겨찾기를 실수로/악의적으로 삭제하지 못하도록 한다.
     *
     * @param id       삭제할 즐겨찾기 항목의 고유 ID
     * @param memberId 삭제를 요청한 회원의 고유 ID (소유자 검증용)
     * @return 실제로 삭제된 row 개수 (0이면 삭제 실패 또는 소유자 불일치)
     */
    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
