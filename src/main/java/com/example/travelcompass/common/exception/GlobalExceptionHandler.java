package com.example.travelcompass.common.exception;

import com.example.travelcompass.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// 모든 REST 컨트롤러의 예외를 한 곳에서 잡아 ApiResponse 형태로 통일해서 응답한다.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 의도적으로 던진 업무 예외 -> ErrorCode에 정의된 상태코드/메시지 그대로 응답
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException: {}", e.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    // @Valid 검증 실패 -> 필드별 에러 메시지 목록을 담아 응답
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("ValidationException: {}", errors);
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    // 그 외 예상하지 못한 모든 예외 -> 500으로 통일 응답 (원인은 로그로만 남김)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
