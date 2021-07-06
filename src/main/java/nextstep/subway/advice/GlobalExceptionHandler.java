package nextstep.subway.advice;

import nextstep.subway.exception.SectionNotConnectedException;
import nextstep.subway.exception.StationNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        LOG.error("GlobalExceptionHandler.handleDataIntegrityViolationException : ", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgsException(IllegalArgumentException e) {
        LOG.error("GlobalExceptionHandler.handleIllegalArgsException : ", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = SectionNotConnectedException.class)
    public ResponseEntity handleSectionNotConnectedException(SectionNotConnectedException e) {
        LOG.error("GlobalExceptionHandler.handleSectionNotConnectedException : ", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = StationNotExistException.class)
    public ResponseEntity handleStationsNotExistException(StationNotExistException e) {
        LOG.error("GlobalExceptionHandler.handleStationsNotExistException : ", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity handleEntityNotFoundException(EntityNotFoundException e) {
        LOG.error("GlobalExceptionHandler.handleEntityNotFoundException : ", e);
        return ResponseEntity.notFound().build();
    }
}