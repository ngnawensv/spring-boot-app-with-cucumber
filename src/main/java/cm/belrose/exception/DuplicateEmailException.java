package cm.belrose.exception;

/**
 * Custom exception thrown when attempting to create a user with a duplicate email.
 * This exception is caught by GlobalExceptionHandler and converted to HTTP 400 response.
 */
public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String message) {
    super(message);
  }

  public DuplicateEmailException(String message, Throwable cause) {
    super(message, cause);
  }
}
