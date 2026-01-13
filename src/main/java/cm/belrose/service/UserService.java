package cm.belrose.service;

import cm.belrose.exception.DuplicateEmailException;
import cm.belrose.exception.UserNotFoundException;
import cm.belrose.model.User;
import cm.belrose.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for User operations.
 *
 * Contains business logic for user management.
 * Sits between Controller (presentation) and Repository (data access).
 *
 * Annotations explained:
 * - @Service: Marks this as a Spring service component
 * - @RequiredArgsConstructor: Lombok generates constructor with final fields (dependency injection)
 * - @Slf4j: Lombok provides a logger instance (log.info(), log.error(), etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  // Injected via constructor (thanks to @RequiredArgsConstructor)
  private final UserRepository userRepository;

  /**
   * Create a new user.
   *
   * Business rule: Email must be unique.
   *
   * @Transactional ensures that if anything fails, the entire operation is rolled back.
   * This maintains data consistency.
   *
   * @param user the user to create
   * @return the created user with generated ID
   * @throws DuplicateEmailException if email already exists
   */
  @Transactional
  public User createUser(User user) {
    log.info("Creating user with email: {}", user.getEmail());

    // Business logic: Check for duplicate email
    if (userRepository.existsByEmail(user.getEmail())) {
      log.error("Email already exists: {}", user.getEmail());
      throw new DuplicateEmailException("Email already exists: " + user.getEmail());
    }

    // Save to database
    User savedUser = userRepository.save(user);
    log.info("User created successfully with ID: {}", savedUser.getId());
    return savedUser;
  }

  /**
   * Get user by ID.
   *
   * @param id the user ID
   * @return the user
   * @throws UserNotFoundException if user doesn't exist
   */
  public User getUserById(Long id) {
    log.info("Fetching user with ID: {}", id);

    // Optional pattern: orElseThrow() converts Optional to value or throws exception
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
  }

  /**
   * Get all users.
   *
   * @return list of all users
   */
  public List<User> getAllUsers() {
    log.info("Fetching all users");
    return userRepository.findAll();
  }

  /**
   * Update user information.
   *
   * @param id the user ID to update
   * @param userDetails the new user details
   * @return the updated user
   * @throws UserNotFoundException if user doesn't exist
   */
  @Transactional
  public User updateUser(Long id, User userDetails) {
    log.info("Updating user with ID: {}", id);

    // First, get the existing user (throws exception if not found)
    User user = getUserById(id);

    // Update fields
    user.setName(userDetails.getName());
    user.setEmail(userDetails.getEmail());
    user.setActive(userDetails.isActive());

    // Save changes (JPA detects changes and updates automatically in @Transactional)
    User updatedUser = userRepository.save(user);
    log.info("User updated successfully");
    return updatedUser;
  }

  /**
   * Delete user.
   *
   * @param id the user ID to delete
   * @throws UserNotFoundException if user doesn't exist
   */
  @Transactional
  public void deleteUser(Long id) {
    log.info("Deleting user with ID: {}", id);

    // Check if user exists before deleting
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException("User not found with id: " + id);
    }

    userRepository.deleteById(id);
    log.info("User deleted successfully");
  }

  /**
   * Deactivate user (soft delete).
   *
   * Sets active flag to false instead of deleting from database.
   *
   * @param id the user ID to deactivate
   * @return the deactivated user
   */
  @Transactional
  public User deactivateUser(Long id) {
    log.info("Deactivating user with ID: {}", id);

    User user = getUserById(id);
    user.setActive(false);

    User deactivatedUser = userRepository.save(user);
    log.info("User deactivated successfully");
    return deactivatedUser;
  }
}