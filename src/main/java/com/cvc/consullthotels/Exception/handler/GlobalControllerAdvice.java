package com.cvc.consullthotels.Exception.handler;

import com.cvc.consullthotels.Exception.CheckInDateInvalidException;
import com.cvc.consullthotels.Exception.CheckOutDateInvalidException;
import com.cvc.consullthotels.Exception.NumberOfClientsException;
import com.cvc.consullthotels.domain.dto.ApiError;
import com.cvc.consullthotels.enums.ErrorType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if(ex instanceof MethodArgumentTypeMismatchException)
            return handleMethodArgumentTypeMismatchException((MethodArgumentTypeMismatchException) ex, headers, status, request);

        String message = messageSource.getMessage(ErrorType.INVALID_FORMAT_GENERIC.getMessageSource(), null, LocaleContextHolder.getLocale()) ;
        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.INVALID_FORMAT_GENERIC.getUri(),
                ErrorType.INVALID_FORMAT_GENERIC.getTitle(), message);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.MISSING_PARAMETER.getMessageSource(), null, LocaleContextHolder.getLocale()) + ex.getParameterName();
        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.MISSING_PARAMETER.getUri(),
                ErrorType.MISSING_PARAMETER.getTitle(), message);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(CheckInDateInvalidException.class)
    protected ResponseEntity<Object> handleMissingCheckInDateInvalidException(CheckInDateInvalidException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.CHECK_IN_DATE_INVALID.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.CHECK_IN_DATE_INVALID.getUri(),
                ErrorType.CHECK_IN_DATE_INVALID.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(CheckOutDateInvalidException.class)
    protected ResponseEntity<Object> handleMissingCheckOutDateInvalidException(CheckOutDateInvalidException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.CHECK_OUT_DATE_INVALID.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.CHECK_OUT_DATE_INVALID.getUri(),
                ErrorType.CHECK_OUT_DATE_INVALID.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NumberOfClientsException.class)
    protected ResponseEntity<Object> handleMissingNumberOfClientsException(NumberOfClientsException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.NUMBER_CLIENTS_INVALID.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.NUMBER_CLIENTS_INVALID.getUri(),
                ErrorType.NUMBER_CLIENTS_INVALID.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                             HttpHeaders headers, HttpStatus status, WebRequest request){
        String message = messageSource.getMessage(ErrorType.INVALID_FORMAT.getMessageSource(), null, LocaleContextHolder.getLocale()) + ex.getName();

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.INVALID_FORMAT.getUri(),
                ErrorType.INVALID_FORMAT.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ApiError crateBodyError(int valueStatus, String type, String title, String detail){
        return ApiError.builder()
                .status(valueStatus)
                .type(type)
                .title(title)
                .detail(detail)
                .dateHour(LocalDateTime.now())
                .build();
    }

//    @ExceptionHandler
//    protected ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
//        List<String> errors = createListErrors(ex.getBindingResult());
//        ApiError apiError = new ApiError(null,null,null);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(apiError);
//    }

//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    protected ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
//        String message = messageSource.getMessage("parameter.not.informed", null, LocaleContextHolder.getLocale()) + ex.getParameterName();
//        ApiError error = new ApiError(Collections.singletonList(message));
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(error);
//    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if(isNull(body)){
            body = ApiError.builder()
                    .dateHour(LocalDateTime.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .build();
        }else if (body instanceof String){
            body = ApiError.builder()
                    .dateHour(LocalDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private List<String> createListErrors(BindingResult bindingResult){
        List<String> errors = new ArrayList<>();

        for(FieldError fieldError : bindingResult.getFieldErrors()) {
            String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            errors.add(message);
        }
        return errors;
    }

}
