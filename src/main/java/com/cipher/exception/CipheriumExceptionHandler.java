package com.cipher.exception;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CipheriumExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(CipheriumExceptionHandler.class);

    public CipheriumExceptionHandler() {
    }

    @Autowired
    private MessageSource messageSource;

    private String getMessage(String messageKey){
        return messageSource.getMessage(messageKey, null, getUserLocale());
    }

    private String getMessage(String messageKey, String[] params){
        return messageSource.getMessage(messageKey, params, getUserLocale());
    }

    @ExceptionHandler({
            ForbiddenException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ExceptionDto handleForbidden(Exception e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler({NotExistException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionDto handleNotFoundException(Exception e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler({DuplicatedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ExceptionDto handleConflictException(Exception e) {
         if(e instanceof DuplicatedException){
            DuplicatedException de = (DuplicatedException) e;
            return new ExceptionDto(de.getMessage());
        }

        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler({
            ParameterException.class,
            BadRequestException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionDto handleParameterException(Exception e) {
        if(e instanceof ParameterException){
            ParameterException pe = (ParameterException) e;
            return new ExceptionDto(getMessage(pe.getMessage(), pe.getParams()));
        }
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler({MessageException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionDto handleBadRequestMessageException(Exception e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public final ExceptionDto handleAccessDeniedException(Exception ex, WebRequest request) {
        return new ExceptionDto("Forbiden");
    }

    @ExceptionHandler(SystemInternalErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public final ExceptionDto handleSystemInternalServerError(Exception ex, WebRequest request) {
        if(ex instanceof SystemInternalErrorException){
            SystemInternalErrorException sie = (SystemInternalErrorException) ex;
            return new ExceptionDto(sie.getMessage());
        }
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public final ExceptionDto handleInternalServerError(Exception ex, WebRequest request) {
        logger.error("Internal Server Error", ex);
        return new ExceptionDto("System error");
    }

    // 改寫成@ExceptionHandler(MethodArgumentNotValidException.class)
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        String[] messages = Objects.requireNonNull(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()).split(",");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto(messageSource.getMessage(messages[0], new String[]{messages[1], messages[2]}, Locale.getDefault())));
//    }

    protected Locale getUserLocale(){
        return LocaleContextHolder.getLocale();
    }

}
