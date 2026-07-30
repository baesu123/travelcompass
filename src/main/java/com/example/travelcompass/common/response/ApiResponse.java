package com.example.travelcompass.common.response;

import com.example.travelcompass.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 모든 REST API 응답을 감싸는 공통 포맷(래퍼) 클래스이다.
 *
 * <p>성공/실패 여부와 관계없이 항상 {@code success, code, message, data} 네 가지 필드로 응답 JSON을
 * 통일해서, 프론트엔드(클라이언트)가 응답 구조를 예측 가능하게 만들어준다.
 * 예: 성공 시 {@code {"success":true,"code":"SUCCESS","message":"...","data":{...}}},
 * 실패 시 {@code {"success":false,"code":"F002","message":"즐겨찾기를 찾을 수 없습니다.","data":null}}.</p>
 *
 * <p>생성자를 private으로 막고 정적 팩토리 메서드({@code success}, {@code fail})만 열어둠으로써
 * 항상 정해진 조합으로만 객체가 만들어지도록 강제한다(무분별한 new 생성 방지).
 * 컨트롤러는 정상 처리 시 {@code ApiResponse.success(data)}를 반환하고,
 * {@link com.example.travelcompass.common.exception.GlobalExceptionHandler}는 예외 발생 시
 * {@code ApiResponse.fail(errorCode)}를 사용해 동일한 포맷으로 응답한다.</p>
 *
 * @param <T> 실제로 담기는 응답 데이터의 타입 (성공 시 결과 데이터, 실패 시 없거나 상세 에러 목록 등)
 */
// 모든 REST API 응답을 감싸는 공통 포맷 (success/code/message/data)
@Getter
public class ApiResponse<T> {

    // 요청 처리 성공 여부 (true: 성공, false: 실패)
    private final boolean success;
    // 결과를 구분하는 코드 문자열. 성공 시 "SUCCESS" 고정, 실패 시 ErrorCode에 정의된 코드(F002 등)
    private final String code;
    // 사용자/클라이언트에게 보여줄 메시지
    private final String message;
    // 실제 응답 데이터. 실패했거나 반환할 데이터가 없으면 null일 수 있다
    private final T data;

    // 외부에서 직접 new로 생성하지 못하도록 막고, 아래 정적 팩토리 메서드를 통해서만 생성하게 한다
    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 기본 성공 메시지("요청이 성공했습니다.")와 함께 성공 응답을 생성한다.
     *
     * @param data 클라이언트에 내려줄 결과 데이터
     * @return success=true, code="SUCCESS"로 세팅된 ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", data);
    }

    /**
     * 메시지를 직접 지정하며 성공 응답을 생성한다.
     *
     * @param message 상황에 맞게 보여줄 커스텀 성공 메시지
     * @param data    클라이언트에 내려줄 결과 데이터
     * @return success=true, code="SUCCESS"로 세팅된 ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data);
    }

    /**
     * data 없이, ErrorCode에 정의된 코드/메시지로 실패 응답을 생성한다.
     * 주로 {@link com.example.travelcompass.common.exception.BusinessException} 처리 시 사용된다.
     *
     * @param errorCode 실패 원인을 나타내는 에러 코드
     * @return success=false로 세팅된 ApiResponse (data는 null)
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * ErrorCode의 코드/메시지를 사용하면서, data에 추가 상세 정보(예: 검증 실패 필드 메시지 목록)를 담아
     * 실패 응답을 생성한다.
     *
     * @param errorCode 실패 원인을 나타내는 에러 코드
     * @param data      실패에 대한 부가 정보(예: 필드별 검증 오류 메시지 리스트)
     * @return success=false로 세팅된 ApiResponse
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T data) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }
}
