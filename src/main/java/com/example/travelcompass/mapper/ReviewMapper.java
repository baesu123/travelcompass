package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 국가별 여행 리뷰(review)를 다루는 Mapper 인터페이스.
 * 특정 국가에 대한 리뷰 목록/단건 조회, 등록, 수정, 삭제 기능을 담당한다.
 *
 * <p>초보자 팁: {@code @Mapper} 인터페이스에는 SQL이 없다. 스프링이 이 인터페이스를 스캔해서
 * 프록시(대리) 구현체를 만들고, 메서드가 호출되면 같은 이름의 {@code ReviewMapper.xml} 안에서
 * 동일한 {@code id}를 가진 SQL 태그를 실행한 뒤 그 결과를 반환값 타입(Review, List 등)으로 변환해 준다.</p>
 */
@Mapper
public interface ReviewMapper {

    /**
     * 특정 국가 코드에 해당하는 모든 리뷰를 조회한다.
     *
     * @param countryCode 조회할 국가 코드 (예: "KR", "JP")
     * @return 해당 국가에 작성된 리뷰 목록 (없으면 빈 리스트)
     */
    List<Review> findAllByCountryCode(String countryCode);

    /**
     * 리뷰 고유 ID(PK)로 리뷰 상세 정보를 조회한다.
     *
     * @param id 조회할 리뷰의 고유 ID
     * @return 일치하는 리뷰 정보 (없으면 null)
     */
    Review findById(Long id);

    /**
     * 새로운 리뷰를 DB에 저장(INSERT)한다.
     *
     * @param review 저장할 리뷰 정보(국가 코드, 작성자 ID, 제목, 내용, 평점 등)를 담은 엔티티 객체
     */
    void insert(Review review);

    /**
     * 기존 리뷰 내용을 수정한다.
     * 전달되는 review 객체 안에 수정할 값과 함께 id, memberId(작성자 검증용)가 포함되어 있어야 한다.
     *
     * @param review 수정할 내용과 작성자 검증 정보를 담은 엔티티 객체
     * @return 실제로 수정된 row 개수 (0이면 수정 실패 또는 작성자 불일치)
     */
    int updateByIdAndMemberId(Review review);

    /**
     * 특정 리뷰를 ID로 삭제한다.
     * memberId 조건을 함께 걸어, 리뷰 작성자 본인만 삭제할 수 있도록 제한한다.
     *
     * @param id       삭제할 리뷰의 고유 ID
     * @param memberId 삭제를 요청한 회원의 고유 ID (작성자 검증용)
     * @return 실제로 삭제된 row 개수 (0이면 삭제 실패 또는 작성자 불일치)
     */
    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
