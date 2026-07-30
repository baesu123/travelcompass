package com.example.travelcompass.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 프로젝트 전역에서 사용하는 에러 코드 모음이다.
 *
 * <p>각 상수는 (HTTP 상태코드, 서비스 내부에서 관리하는 코드 문자열, 사용자에게 보여줄 기본 메시지)
 * 세 가지 정보를 하나로 묶어 가지고 있다. {@link BusinessException}을 던질 때 이 상수 중 하나를 골라서
 * 넘기면, {@link GlobalExceptionHandler}가 status/code/message를 꺼내
 * {@link com.example.travelcompass.common.response.ApiResponse}에 담아 응답한다.</p>
 *
 * <p>코드 문자열은 접두사로 도메인을 구분한다: C(공통/Common), M(회원/Member), F(즐겨찾기/Favorite),
 * CL(체크리스트/Checklist), R(후기·댓글/Review·Comment) 등. 새로운 에러 상황이 생기면
 * 여기에 상수를 추가해서 재사용하면 된다.</p>
 */
// 프로젝트 전역에서 사용하는 에러 코드 모음 (HTTP 상태 + 코드 + 메시지)
@Getter
public enum ErrorCode {

    // ===== 공통(Common) 에러: 특정 도메인에 속하지 않는 범용 에러 =====
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력값입니다."), // @Valid 검증 실패 시 사용
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."), // 예상치 못한 예외의 기본값

    // ===== 회원(Member) 도메인 에러 =====
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "M001", "이미 사용 중인 아이디입니다."),

    // ===== 즐겨찾기(Favorite) 도메인 에러 =====
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "F001", "이미 즐겨찾기에 추가된 국가입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "즐겨찾기를 찾을 수 없습니다."),

    // ===== 체크리스트(Checklist) 도메인 에러 =====
    CHECKLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "CL001", "체크리스트 항목을 찾을 수 없습니다."),

    // ===== 후기/댓글(Review/Comment) 도메인 에러 =====
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "후기를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "댓글을 찾을 수 없습니다.");

    // 클라이언트에게 내려줄 HTTP 상태 코드 (예: 400, 404, 409, 500)
    private final HttpStatus status;
    // 프론트/클라이언트가 상태코드보다 세밀하게 분기 처리할 때 사용할 수 있는 서비스 내부 코드 문자열
    private final String code;
    // 사용자/개발자에게 보여줄 기본 에러 메시지
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
