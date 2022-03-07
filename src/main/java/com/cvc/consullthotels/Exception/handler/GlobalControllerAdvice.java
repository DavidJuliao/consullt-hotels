package com.cvc.consullthotels.Exception.handler;

import com.cvc.consullthotels.Exception.CheckInDateInvalidException;
import com.cvc.consullthotels.Exception.CheckOutDateInvalidException;
import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.Exception.FeignGeneralException;
import com.cvc.consullthotels.Exception.HotelInformationNotFoundException;
import com.cvc.consullthotels.Exception.NumberOfClientsException;
import com.cvc.consullthotels.domain.dto.ApiError;
import com.cvc.consullthotels.enums.ErrorType;
import feign.FeignException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

import static com.cvc.consullthotels.domain.dto.ApiError.crateBodyError;
import static com.cvc.consullthotels.enums.ErrorType.CONSULT_HOTEL_INFORMATION;
import static com.cvc.consullthotels.enums.ErrorType.GOOGLE_TOKEN_INVALID;
import static com.cvc.consullthotels.enums.ErrorType.GOOGLE_TOKEN_VALIDATION_ERROR;
import static com.cvc.consullthotels.enums.ErrorType.HOTEL_INFORMATION_NOT_FOUNT;
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
    protected ResponseEntity<Object> handleCheckInDateInvalidException(CheckInDateInvalidException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.CHECK_IN_DATE_INVALID.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.CHECK_IN_DATE_INVALID.getUri(),
                ErrorType.CHECK_IN_DATE_INVALID.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(CheckOutDateInvalidException.class)
    protected ResponseEntity<Object> handleCheckOutDateInvalidException(CheckOutDateInvalidException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.CHECK_OUT_DATE_INVALID.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.CHECK_OUT_DATE_INVALID.getUri(),
                ErrorType.CHECK_OUT_DATE_INVALID.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NumberOfClientsException.class)
    protected ResponseEntity<Object> handleNumberOfClientsException(NumberOfClientsException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.NUMBER_CLIENTS_INVALID.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.NUMBER_CLIENTS_INVALID.getUri(),
                ErrorType.NUMBER_CLIENTS_INVALID.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConsultHotelInformationException.class)
    protected ResponseEntity<Object> handleConsultHotelInformationException(ConsultHotelInformationException ex, WebRequest request) {
        String message = messageSource.getMessage(CONSULT_HOTEL_INFORMATION.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.INTERNAL_SERVER_ERROR.value(), CONSULT_HOTEL_INFORMATION.getUri(),
                CONSULT_HOTEL_INFORMATION.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(HotelInformationNotFoundException.class)
    protected ResponseEntity<ApiError> handleHotelInformationNotFoundException(HotelInformationNotFoundException ex, WebRequest request) {
        String message = messageSource.getMessage(HOTEL_INFORMATION_NOT_FOUNT.getMessageSource(), null, LocaleContextHolder.getLocale()) + ex.getMessage();

        ApiError body = crateBodyError(HttpStatus.NO_CONTENT.value(), HOTEL_INFORMATION_NOT_FOUNT.getUri(),
                HOTEL_INFORMATION_NOT_FOUNT.getTitle(), message);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(body);
    }

    @ExceptionHandler(FeignGeneralException.class)
    protected ResponseEntity<Object> handleGoogleTokenInvalidException(FeignGeneralException ex, WebRequest request) {
        ErrorType errorType;

        if(ex.request().url().contains("id_token")){
            errorType = ex.status() <= 499 ?GOOGLE_TOKEN_INVALID:GOOGLE_TOKEN_VALIDATION_ERROR;
        }else{
            errorType = ex.status() <= 499 ?HOTEL_INFORMATION_NOT_FOUNT:CONSULT_HOTEL_INFORMATION;
        }
        HttpStatus status = Arrays.stream(HttpStatus.values())
                                .filter(httpStatus -> httpStatus.value() == ex.status())
                                .findAny()
                                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        String message = messageSource.getMessage(errorType.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(ex.status(), errorType.getUri(),
                errorType.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    protected ResponseEntity<Object> handleGoogleTokenInvalidException(RedisConnectionFailureException ex, WebRequest request) {
        String message = messageSource.getMessage(ErrorType.REDIS_CONNECTION_ERROR.getMessageSource(), null, LocaleContextHolder.getLocale());

        ApiError body = crateBodyError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorType.REDIS_CONNECTION_ERROR.getUri(),
                ErrorType.REDIS_CONNECTION_ERROR.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                             HttpHeaders headers, HttpStatus status, WebRequest request){
        String message = messageSource.getMessage(ErrorType.INVALID_FORMAT.getMessageSource(), null, LocaleContextHolder.getLocale()) + ex.getName();

        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.INVALID_FORMAT.getUri(),
                ErrorType.INVALID_FORMAT.getTitle(), message);

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if(isNull(body)){
            body = ApiError.builder()
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .build();
        }else if (body instanceof String){
            body = ApiError.builder()
                    .title((String) body)
                    .status(status.value())
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

}
