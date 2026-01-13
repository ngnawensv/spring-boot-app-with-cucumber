package cm.belrose.controller;

import cm.belrose.model.User;
import cm.belrose.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User operations.
 * Handles HTTP requests and returns HTTP responses.
 * This is the presentation layer - it doesn't contain business logic.
 * Annotations explained:
 * - @RestController: Combines @Controller and @ResponseBody (returns data, not views)
 * - @RequestMapping: Base path for all endpoints in this controller
 * - @RequiredArgsConstructor: Lombok generates constructor for dependency injection
 * - @Slf4j: Lombok provides logger
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  /**
   * Create a new user.
   * POST /api/users
   * @Valid triggers validation on the User object (@NotBlank, @Email, etc.)
   * If validation fails, MethodArgumentNotValidException is thrown and handled by GlobalExceptionHandler
   *
   * @param user the user to create (from request body)
   * @return HTTP 201 Created with the created user
   */
  @PostMapping
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    log.info("REST request to create user: {}", user.getEmail());
    User createdUser = userService.createUser(user);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  /**
   * Get user by ID.
   * GET /api/users/{id}
   * @PathVariable extracts {id} from URL path
   * Example: GET /api/users/5 → id = 5
   * @param id the user ID from URL path
   * @return HTTP 200 OK with the user
   */
  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    log.info("REST request to get user with ID: {}", id);
    User user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  /**
   * Get all users.
   * GET /api/users
   * @return HTTP 200 OK with list of all users
   */
  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    log.info("REST request to get all users");
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Update user
   * PUT /api/users/{id}
   * PUT is used for full updates (all fields).
   * PATCH would be for partial updates.
   *
   * @param id the user ID to update
   * @param userDetails the new user details
   * @return HTTP 200 OK with updated user
   */
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id,
      @Valid @RequestBody User userDetails) {
    log.info("REST request to update user with ID: {}", id);
    User updatedUser = userService.updateUser(id, userDetails);
    return ResponseEntity.ok(updatedUser);
  }

  /**
   * Delete user.
   * DELETE /api/users/{id}
   * Returns 204 No Content on success (no response body needed for delete).
   * @param id the user ID to delete
   * @return HTTP 204 No Content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    log.info("REST request to delete user with ID: {}", id);
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Deactivate user.
   * PATCH /api/users/{id}/deactivate
   * PATCH is used for partial updates (only changing active status).
   * @param id the user ID to deactivate
   * @return HTTP 200 OK with deactivated user
   */
  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
    log.info("REST request to deactivate user with ID: {}", id);
    User deactivatedUser = userService.deactivateUser(id);
    return ResponseEntity.ok(deactivatedUser);
  }
}