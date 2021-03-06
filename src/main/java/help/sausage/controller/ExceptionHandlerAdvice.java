package help.sausage.controller;

import help.sausage.dto.ErrorDto;
import help.sausage.exceptions.UnknownUsernameException;
import help.sausage.exceptions.UsernameAlreadyExistException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        String msg = "Validation failed for argument ["
                + ex.getParameter().getParameterIndex() + "] in "
                + ex.getParameter().getExecutable().toGenericString();
        ErrorDto model = new ErrorDto(msg, request.getContextPath());
        model.getMeta().put("target", ex.getTarget());
        model.getMeta().put("violations", ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList());
        return ResponseEntity.status(status).body(model);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUsernameNotFound(HttpServletRequest req, UsernameNotFoundException ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getMessage(), req.getContextPath()), HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorDto> handleNoSuchElement(HttpServletRequest req, NoSuchElementException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), req.getContextPath());
        log.error("No value present", ex);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleUsernameExist(HttpServletRequest req, UsernameAlreadyExistException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), req.getContextPath());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownUsernameException.class)
    public ResponseEntity<ErrorDto> handleUnknowneUsername(HttpServletRequest req, UnknownUsernameException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), req.getContextPath());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(HttpServletRequest req, Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), req.getContextPath());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
