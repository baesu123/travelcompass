package com.example.travelcompass.common.exception;

import lombok.Getter;

// 서비스 로직에서 발생하는 업무 예외의 공통 타입. ErrorCode로 상태코드/메시지를 함께 전달한다.
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
