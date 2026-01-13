package cm.belrose.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing a user in the system.
 *
 * This is a JPA entity that maps to the 'users' table in the database.
 *
 * Annotations explained:
 * - @Entity: Marks this class as a JPA entity (database table)
 * - @Table: Specifies the table name (optional, defaults to class name)
 * - @Data: Lombok annotation that generates getters, setters, toString, equals, and hashCode
 * - @NoArgsConstructor: Lombok generates a no-argument constructor (required by JPA)
 * - @AllArgsConstructor: Lombok generates a constructor with all fields
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  /**
   * Primary key - auto-generated ID
   * @GeneratedValue with IDENTITY strategy means database generates the ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * User's name - required field
   * @NotBlank ensures the field is not null, empty, or just whitespace
   */
  @NotBlank(message = "Name is required")
  @Column(nullable = false)
  private String name;

  /**
   * User's email - required and must be unique
   * @Email validates email format
   * unique = true ensures no duplicate emails in database
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(nullable = false, unique = true)
  private String email;

  /**
   * User's active status - defaults to true
   */
  @Column
  private boolean active = true;

  /**
   * Convenience constructor without ID (used when creating new users)
   */
  public User(String name, String email) {
    this.name = name;
    this.email = email;
    this.active = true;
  }
}