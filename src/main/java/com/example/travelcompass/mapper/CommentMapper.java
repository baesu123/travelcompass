package com.example.travelcompass.mapper;

import com.example.travelcompass.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 리뷰(review)에 달리는 댓글(comment)을 다루는 Mapper 인터페이스.
 * 특정 리뷰에 달린 댓글 조회, 등록, 내용 수정, 삭제 기능을 담당한다.
 *
 * <p>초보자 팁: 이 인터페이스는 SQL 없이 메서드 시그니처만 선언되어 있다.
 * 실제 SQL은 {@code CommentMapper.xml}에서 같은 이름의 {@code id}를 가진 태그로 정의되어 있으며,
 * MyBatis가 메서드 호출 시 해당 SQL을 찾아 실행하고 결과를 자바 객체로 변환해 돌려준다.</p>
 */
@Mapper
public interface CommentMapper {

    /**
     * 특정 리뷰에 달린 모든 댓글을 조회한다.
     *
     * @param reviewId 조회할 리뷰의 고유 ID
     * @return 해당 리뷰의 댓글 목록 (없으면 빈 리스트)
     */
    List<Comment> findAllByReviewId(Long reviewId);

    /**
     * 새로운 댓글을 DB에 저장(INSERT)한다.
     *
     * @param comment 저장할 댓글 정보(리뷰 ID, 작성자 ID, 내용 등)를 담은 엔티티 객체
     */
    void insert(Comment comment);

    /**
     * 특정 댓글의 내용을 수정한다.
     * memberId 조건을 함께 걸어, 댓글 작성자 본인만 수정할 수 있도록 제한한다.
     *
     * @param id       수정할 댓글의 고유 ID
     * @param memberId 수정을 요청한 회원의 고유 ID (작성자 검증용)
     * @param content  변경할 댓글 내용
     * @return 실제로 수정된 row 개수 (0이면 수정 실패 또는 작성자 불일치)
     */
    int updateContentByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId, @Param("content") String content);

    /**
     * 특정 댓글을 ID로 삭제한다.
     * memberId 조건을 함께 걸어, 댓글 작성자 본인만 삭제할 수 있도록 제한한다.
     *
     * @param id       삭제할 댓글의 고유 ID
     * @param memberId 삭제를 요청한 회원의 고유 ID (작성자 검증용)
     * @return 실제로 삭제된 row 개수 (0이면 삭제 실패 또는 작성자 불일치)
     */
    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

}
