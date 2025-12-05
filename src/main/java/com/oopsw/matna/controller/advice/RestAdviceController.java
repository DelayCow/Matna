package com.oopsw.matna.controller.advice;

import com.oopsw.matna.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// try catch 모아서 관리
@RestControllerAdvice
public class RestAdviceController {

    // 1. IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage(), "BAD_REQUEST");
    }

    // 2. NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ErrorResponse handleNullPointer(NullPointerException e) {
        return new ErrorResponse("서버 내부에서 NullPointerException 발생", "NULL_POINTER");
    }

    // 3. 비즈니스 예외
    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustom(CustomException e) {
        return new ErrorResponse(e.getMessage(), e.getCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntime(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), "RUNTIME_EXCEPTION");
    }

    // 4. 모든 예외 처리(최후방)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse("알 수 없는 오류가 발생했습니다.", "UNKNOWN_ERROR");
    }
}
