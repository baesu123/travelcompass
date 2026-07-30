package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Checklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 회원의 여행 준비물 체크리스트(checklist) 항목을 다루는 Mapper 인터페이스.
 * 항목 조회, 등록, 체크 여부(완료/미완료) 수정, 삭제 기능을 담당한다.
 *
 * <p>초보자 팁: {@code @Mapper} 인터페이스는 실제 SQL을 갖고 있지 않다.
 * 스프링이 이 인터페이스의 구현체를 실행 시점에 자동으로 만들어 주는데,
 * 실제 동작(SQL)은 같은 이름의 {@code ChecklistMapper.xml} 파일에서
 * 메서드 이름과 동일한 {@code id}를 가진 태그를 찾아 수행한다.</p>
 */
@Mapper
public interface ChecklistMapper {

    /**
     * 특정 회원이 등록한 모든 체크리스트 항목을 조회한다.
     *
     * @param memberId 조회할 회원의 고유 ID
     * @return 해당 회원의 체크리스트 항목 목록 (없으면 빈 리스트)
     */
    List<Checklist> findAllByMemberId(Long memberId);

    /**
     * 새로운 체크리스트 항목을 DB에 저장(INSERT)한다.
     *
     * @param checklist 저장할 체크리스트 정보(회원 ID, 항목명, 체크 여부 등)를 담은 엔티티 객체
     */
    void insert(Checklist checklist);

    /**
     * 특정 체크리스트 항목의 체크 상태(완료/미완료)를 수정한다.
     * memberId 조건을 함께 걸어, 본인 소유의 항목만 수정할 수 있도록 제한한다.
     *
     * @param id       수정할 체크리스트 항목의 고유 ID
     * @param memberId 수정을 요청한 회원의 고유 ID (소유자 검증용)
     * @param checked  변경할 체크 상태 값 (true: 체크됨, false: 체크 해제)
     * @return 실제로 수정된 row 개수 (0이면 수정 실패 또는 소유자 불일치)
     */
    int updateCheckedByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId, @Param("checked") boolean checked);

    /**
     * 특정 체크리스트 항목을 ID로 삭제한다.
     * memberId 조건을 함께 걸어 본인 소유의 항목만 삭제할 수 있도록 제한한다.
     *
     * @param id       삭제할 체크리스트 항목의 고유 ID
     * @param memberId 삭제를 요청한 회원의 고유 ID (소유자 검증용)
     * @return 실제로 삭제된 row 개수 (0이면 삭제 실패 또는 소유자 불일치)
     */
    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
