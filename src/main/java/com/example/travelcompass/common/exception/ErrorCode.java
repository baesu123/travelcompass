package com.example.travelcompass.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "M001", "이미 사용 중인 아이디입니다."),

    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "F001", "이미 즐겨찾기에 추가된 국가입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "즐겨찾기를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
