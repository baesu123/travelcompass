package com.example.travelcompass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 후기(리뷰)에 댓글을 작성할 때 클라이언트로부터 전달받는 데이터를 담는 DTO이다.
 *
 * <p>컨트롤러에서 {@code @Valid}와 함께 사용되어야 {@code @NotBlank} 검증이 실제로 동작하며,
 * 값이 비어있으면 요청이 컨트롤러 로직에 도달하기 전에 걸러진다.</p>
 */
@Getter
@Setter
public class CommentCreateRequest {

    // 댓글 내용. @NotBlank: null, 빈 문자열, 공백만 있는 문자열을 모두 검증 실패로 처리
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

}
