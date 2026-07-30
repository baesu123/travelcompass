package com.example.travelcompass.common.exception;

import com.example.travelcompass.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 애플리케이션의 모든 REST 컨트롤러에서 발생하는 예외를 한 곳에서 처리하는 전역 예외 처리기이다.
 *
 * <p><b>전체 흐름</b>: 컨트롤러(또는 그 아래 서비스 계층)에서 예외가 발생하면, 스프링이 예외 타입에 맞는
 * {@code @ExceptionHandler} 메서드를 이 클래스에서 찾아 실행시킨다. 각 핸들러는 예외 정보를 바탕으로
 * {@link com.example.travelcompass.common.response.ApiResponse}(공통 응답 포맷)를 만들어 클라이언트에게
 * 돌려준다. 즉 "컨트롤러에서 예외 발생 → @ExceptionHandler가 가로챔 → 상태코드/메시지를 정리해서
 * ApiResponse로 응답"이 이 클래스의 핵심 동작 원리다.</p>
 *
 * <p>{@literal @}{@link RestControllerAdvice}가 붙어 있어 프로젝트의 모든 {@code @RestController}에
 * 전역으로 적용되며, 각 컨트롤러마다 try-catch를 반복해서 작성할 필요가 없어진다.</p>
 */
// 모든 REST 컨트롤러의 예외를 한 곳에서 잡아 ApiResponse 형태로 통일해서 응답한다.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스 로직에서 의도적으로 던진 {@link BusinessException}을 처리한다.
     * (예: 존재하지 않는 데이터 조회, 중복된 값 등록 시도 등 이미 예상된 에러 상황)
     *
     * <p>예외에 담겨있던 {@link ErrorCode}를 그대로 꺼내어 HTTP 상태코드와 메시지를 결정하므로,
     * 새로운 에러 상황이 생기면 ErrorCode에 상수만 추가하면 되고 이 메서드는 수정할 필요가 없다.</p>
     *
     * @param e 서비스 계층 등에서 던져진 업무 예외
     * @return ErrorCode에 정의된 HTTP 상태와 함께, 실패를 나타내는 ApiResponse
     */
    // 의도적으로 던진 업무 예외 -> ErrorCode에 정의된 상태코드/메시지 그대로 응답
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException: {}", e.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    /**
     * 요청 DTO에 붙은 Bean Validation 애노테이션(@NotBlank, @Size 등)의 검증이 실패했을 때 발생하는
     * {@link MethodArgumentNotValidException}을 처리한다.
     *
     * <p>컨트롤러 메서드 파라미터에 {@code @Valid}가 붙어 있으면, 스프링이 요청 바디를 DTO로 바인딩한 뒤
     * 자동으로 필드 검증을 수행한다. 이때 하나라도 검증에 실패하면 컨트롤러 코드는 실행조차 되지 않고
     * 바로 이 예외가 발생하여 이 핸들러로 넘어온다. 즉 개발자가 직접 if문으로 값 검증을 하지 않아도
     * "@Valid + 검증 애노테이션 + 이 핸들러" 조합만으로 입력값 검증과 에러 응답이 자동 처리된다.</p>
     *
     * @param e 필드별 검증 실패 정보를 담고 있는 예외
     * @return 검증에 실패한 필드들의 메시지 목록을 data로 담은 ApiResponse (400 Bad Request)
     */
    // @Valid 검증 실패 -> 필드별 에러 메시지 목록을 담아 응답
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(MethodArgumentNotValidException e) {
        // 실패한 필드 에러들에서 각 필드에 지정된 메시지(예: @NotBlank(message="..."))만 뽑아 리스트로 변환
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("ValidationException: {}", errors);
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    /**
     * 위에서 잡지 못한, 예상하지 못한 모든 예외(NPE, DB 오류 등)를 마지막에 잡아주는 안전망이다.
     *
     * <p>사용자에게는 내부 구현 정보(스택 트레이스 등)를 노출하지 않고 500 상태와 일반적인 메시지만
     * 응답하며, 실제 원인은 서버 로그({@code log.error})에만 상세히 남긴다.</p>
     *
     * @param e 처리되지 않은 예외
     * @return 500 Internal Server Error 상태와 함께, 실패를 나타내는 ApiResponse
     */
    // 그 외 예상하지 못한 모든 예외 -> 500으로 통일 응답 (원인은 로그로만 남김)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
