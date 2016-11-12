package sk.stu.fei.mproj.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import sk.stu.fei.mproj.services.StorageService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@ControllerAdvice
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final HttpServletRequest httpServletRequest;
    private final ObjectMapper jsonMapper;

    @Autowired
    public IndexController(ObjectMapper jsonMapper, HttpServletRequest httpServletRequest) {
        this.jsonMapper = jsonMapper;
        this.httpServletRequest = httpServletRequest;
    }

    @RequestMapping(value = {"/app", "/app/"}, method = RequestMethod.GET)
    public RedirectView redirectApp() {
        return new RedirectView("/app/index.html");
    }

    @ExceptionHandler({Throwable.class, Exception.class})
    public void serverError(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public void badCredentials(Exception ex, HttpServletResponse response) throws IOException {
        handleException(new BadCredentialsException("Incorrect Credentials", ex), HttpStatus.UNAUTHORIZED, response);
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public void unauthorized(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.UNAUTHORIZED, response);
    }

    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public void forbidden(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.FORBIDDEN, response);
    }

    @ExceptionHandler({EntityNotFoundException.class, StorageService.FileNotFoundException.class})
    public void notFound(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.NOT_FOUND, response);
    }

    @ExceptionHandler({NullPointerException.class, DataIntegrityViolationException.class, ConstraintViolationException.class, IllegalArgumentException.class, IllegalStateException.class, MethodArgumentNotValidException.class})
    public void integrityViolation(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.UNPROCESSABLE_ENTITY, response);
    }

    private void handleException(Exception ex, HttpStatus status, HttpServletResponse resp) throws IOException {
        logger.error("Error processing request", ex);
        resp.setStatus(status.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final String exceptionMessage = StringUtils.trim(StringUtils.isEmpty(ex.getMessage()) ? status.getReasonPhrase() : ex.getMessage());
        final ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setError(exceptionMessage);
        errorMessage.setException(ex.getClass().getCanonicalName());
        errorMessage.setMessage(exceptionMessage);
        errorMessage.setStatus(status.value());
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setPath(httpServletRequest.getServletPath());
        resp.getOutputStream().print(jsonMapper.writeValueAsString(errorMessage));
    }

    @Data
    private static class ErrorMessage {
        private long timestamp;
        private int status;
        private String error;
        private String exception;
        private String message;
        private String path;
    }
}
