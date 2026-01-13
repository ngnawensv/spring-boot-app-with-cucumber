package cm.belrose.exception;

/**
 * Custom exception thrown when a user is not found.
 * Extends RuntimeException (unchecked exception) so it doesn't need to be declared in method signatures.
 * This exception is caught by GlobalExceptionHandler and converted to HTTP 404 response.
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
