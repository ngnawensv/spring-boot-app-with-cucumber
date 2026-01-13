package cm.belrose.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 *
 * @RestControllerAdvice applies to all @RestController classes in the application.
 * Converts exceptions into proper HTTP responses with appropriate status codes and error messages.
 * This centralizes error handling logic instead of repeating it in every controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle UserNotFoundException.
   *
   * Converts to HTTP 404 Not Found response.
   *
   * @param ex the exception
   * @return error response with 404 status
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle DuplicateEmailException.
   * <p>
   * Converts to HTTP 400 Bad Request response.
   *
   * @param ex the exception
   * @return error response with 400 status
   */
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle validation errors (from @Valid annotation).
   * Triggered when request body fails validation (e.g., @NotBlank, @Email).
   * Returns a map of field names to error messages.
   * Example response:
   * {
   *   "name": "Name is required",
   *   "email": "Email should be valid"
   * }
   *
   * @param ex the validation exception
   * @return map of field errors with 400 status
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    // Extract all validation errors
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Catch-all handler for unexpected exceptions.
   * Prevents stack traces from leaking to clients.
   * Returns HTTP 500 Internal Server Error.
   *
   * @param ex any unhandled exception
   * @return generic error response with 500 status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "An unexpected error occurred: " + ex.getMessage(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Error response DTO (Data Transfer Object).
   * Java 17 record - immutable data class with automatic:
   * - Constructor
   * - Getters
   * - equals(), hashCode(), toString()
   * Serialized to JSON automatically by Spring:
   * {
   *   "status": 404,
   *   "message": "User not found",
   *   "timestamp": "2024-01-15T10:30:00"
   * }
   */
  public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}