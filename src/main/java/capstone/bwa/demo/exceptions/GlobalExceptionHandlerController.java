package capstone.bwa.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(CustomException.class)
    public void handleCustomException(HttpServletResponse res, CustomException ex) throws IOException {
        res.sendError(ex.getHttpStatus().value(), ex.getMessage());
    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public void handleAccessDeniedException(HttpServletResponse res) throws IOException {
//        res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
//    }
//
//    @ExceptionHandler(Exception.class)
//    public void handleException(HttpServletResponse res) throws IOException {
//        res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
//    }
}
