package cm.belrose.repository;

import cm.belrose.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 *
 * Spring Data JPA automatically implements this interface at runtime.
 * No implementation code needed!
 *
 * JpaRepository<User, Long> means:
 * - User: The entity type
 * - Long: The type of the entity's primary key (ID)
 *
 * This gives us built-in methods:
 * - save(User user)
 * - findById(Long id)
 * - findAll()
 * - deleteById(Long id)
 * - count()
 * - existsById(Long id)
 * And many more...
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Find user by email address.
   *
   * Spring Data JPA automatically implements this method based on the method name.
   * The naming convention "findBy[FieldName]" tells Spring to query by that field.
   *
   * @param email the email to search for
   * @return Optional containing the user if found, empty Optional otherwise
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if a user exists with the given email.
   *
   * More efficient than findByEmail when you only need to check existence.
   *
   * @param email the email to check
   * @return true if a user with this email exists, false otherwise
   */
  boolean existsByEmail(String email);
}
