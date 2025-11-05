package com.malaka.aat.core.exception;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.*;
import com.malaka.aat.core.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception e,  HttpServletRequest request) {
//        System.err.println(e.toString());
//        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.CONFLICT, e.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
//    }

    @ExceptionHandler(ClientException.class)
    public BaseResponse handleClientException(ClientException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.CLIENT_ERROR);
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        response.setData(errorResponse);
        return response;
    }

    @ExceptionHandler(AuthException.class)
    public BaseResponse handleAuthException(AuthException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.VALIDATION_ERROR);
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        response.setData(errorResponse);
        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.VALIDATION_ERROR);
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        response.setData(errorResponse);
        return response;
    }

    @ExceptionHandler(NotFoundException.class)
    public BaseResponse handleNotFoundException(NotFoundException e,  HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.NOT_FOUND_ERROR);
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.NOT_FOUND, e.getMessage());
        response.setData(errorResponse);
        return response;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public BaseResponse handleAlreadyExistsException(AlreadyExistsException e,  HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.DUPLICATE_ERROR);
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        response.setData(errorResponse);
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handlePropertyReferenceException(MethodArgumentNotValidException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        Map<String, String> fieldErrorResp = new HashMap<>();
        fieldErrors.forEach(fieldError -> {
            fieldErrorResp.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        //ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        response.setData(fieldErrorResp);
        ResponseUtil.setResponseStatus(response, ResponseStatus.VALIDATION_ERROR);
        return response;
    }

    @ExceptionHandler(LoginException.class)
    public BaseResponse handleLoginException(LoginException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.FORBIDDEN, e.getMessage());
        response.setData(errorResponse);
        ResponseUtil.setResponseStatus(response, ResponseStatus.LOGIN_ERROR);
        return response;
    }

    @ExceptionHandler(SystemException.class)
    public BaseResponse handleSystemException(SystemException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        response.setData(errorResponse);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SYSTEM_ERROR);
        return response;
    }

    @ExceptionHandler(BadRequestException.class)
    public BaseResponse handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        response.setData(errorResponse);
        ResponseUtil.setResponseStatus(response,  ResponseStatus.BAD_REQUEST);
        return response;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public BaseResponse handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.FORBIDDEN, e.getMessage());
        response.setData(errorResponse);
        ResponseUtil.setResponseStatus(response,  ResponseStatus.FORBIDDEN);
        response.setData(errorResponse);
        return response;
    }

    @ExceptionHandler(JwtException.class)
    public BaseResponse handleAccessDeniedException(JwtException e, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        ErrorResponse errorResponse = buildErrResponse(request, HttpStatus.FORBIDDEN, e.getMessage());
        response.setData(errorResponse);
        ResponseUtil.setResponseStatus(response,  ResponseStatus.JWT_ERROR);
        response.setData(errorResponse);
        return response;
    }



    private ErrorResponse buildErrResponse(HttpServletRequest request, HttpStatus status,  String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setMethod(request.getMethod());
        errorResponse.setStatus(status.toString());
        return errorResponse;
        //return ResponseEntity.status(status).body(errorResponse);
    }



}
