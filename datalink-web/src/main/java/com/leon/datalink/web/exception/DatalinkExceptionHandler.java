

package com.leon.datalink.web.exception;

import com.leon.datalink.core.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler for console module.
 *
 * @author Leon
 */
@ControllerAdvice
public class DatalinkExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatalinkExceptionHandler.class);
    
    @ExceptionHandler(AccessException.class)
    private ResponseEntity<String> handleAccessException(AccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getErrMsg());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionUtil.getAllExceptionMsg(e));
    }
    
    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> handleException(Exception e) {
        LOGGER.error("WEB", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionUtil.getAllExceptionMsg(e));
    }
}
