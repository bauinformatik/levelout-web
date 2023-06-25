package com.levelout.web.config;

import javax.servlet.http.HttpServletRequest;

import org.bimserver.shared.exceptions.ServiceException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "Internal Server error. Please try after some time or contact admin.";

    private static final String SERVICE_ERROR_MESSAGE = "Currently this service is unavailable. Please try after some time or contact admin.";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable e) {
        HttpStatus status = getStatus(request);
        e.printStackTrace();
        return new ResponseEntity<>(getMessage(e), status);
    }

    private String getMessage(Throwable e) {
        if(e instanceof UserException) {
            return e.getMessage();
        } else if(e instanceof ServiceException) {
            return SERVICE_ERROR_MESSAGE;
        }
        return DEFAULT_ERROR_MESSAGE;
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}