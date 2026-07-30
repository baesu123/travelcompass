package com.example.travelcompass.common.exception;

import lombok.Getter;

/**
 * 서비스(비즈니스) 로직 중에 "의도적으로" 발생시키는 예외의 공통 타입이다.
 *
 * <p>흐름: 서비스 계층에서 특정 상황(예: 중복 아이디, 존재하지 않는 데이터 등)을 감지하면
 * {@code throw new BusinessException(ErrorCode.XXX)} 형태로 이 예외를 던진다.
 * 이 예외는 {@link GlobalExceptionHandler}가 가로채서(@ExceptionHandler(BusinessException.class))
 * {@link ErrorCode}에 정의된 HTTP 상태코드/코드/메시지를 그대로 사용해
 * {@link com.example.travelcompass.common.response.ApiResponse} 형태로 클라이언트에 응답한다.</p>
 *
 * <p>즉 컨트롤러/서비스 코드 곳곳에서 try-catch로 예외를 처리할 필요 없이,
 * 이 예외만 던지면 전역 핸들러가 일관된 에러 응답 포맷을 만들어준다.</p>
 */
// 서비스 로직에서 발생하는 업무 예외의 공통 타입. ErrorCode로 상태코드/메시지를 함께 전달한다.
@Getter
public class BusinessException extends RuntimeException {

    // 예외가 어떤 종류의 에러인지(HTTP 상태, 코드, 기본 메시지)를 담고 있는 열거형 값
    private final ErrorCode errorCode;

    /**
     * ErrorCode에 정의된 기본 메시지를 그대로 예외 메시지로 사용하는 생성자.
     *
     * @param errorCode 발생한 에러의 종류(상태코드/코드/메시지)를 나타내는 열거형 상수
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode는 유지하되, 상황에 맞는 커스텀 메시지를 별도로 지정하고 싶을 때 사용하는 생성자.
     *
     * @param errorCode 발생한 에러의 종류(상태코드/코드)를 나타내는 열거형 상수
     * @param message   기본 메시지 대신 사용할 상세 메시지
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
