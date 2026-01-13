@UserManagement
Feature: User Management
  As a system administrator
  I want to manage users in the system
  So that I can control access and maintain user data

  Background:
    Given the application is running

  @Smoke @Critical
  Scenario: Successfully create a new user
    Given I have user details with name "John Doe" and email "john.doe@example.com"
    When I create the user via REST API
    Then the user should be created with status 201
    And the user should have an ID
    And the user should have name "John Doe"
    And the user should have email "john.doe@example.com"

  @Smoke @Critical
  Scenario: Cannot create user with duplicate email
    Given a user already exists with name "Jane Smith" and email "jane@example.com"
    When I try to create another user with name "John Doe" and email "jane@example.com"
    Then the creation should fail with status 400
    And the error message should contain "Email already exists"

  @Regression
  Scenario: Successfully retrieve user by ID
    Given a user exists with name "Alice Brown" and email "alice@example.com"
    When I retrieve the user by ID
    Then the response status should be 200
    And the retrieved user should have name "Alice Brown"
    And the retrieved user should have email "alice@example.com"

  @Regression
  Scenario: Retrieve non-existent user returns 404
    When I try to retrieve a user with ID 999
    Then the response status should be 404
    And the error message should contain "User not found"

  @Smoke
  Scenario: Successfully retrieve all users
    Given the following users exist:
      | name          | email                  |
      | John Doe      | john@example.com       |
      | Jane Smith    | jane@example.com       |
      | Bob Johnson   | bob@example.com        |
    When I retrieve all users
    Then the response status should be 200
    And I should receive 3 users

  @Regression
  Scenario: Successfully update user information
    Given a user exists with name "Tom Wilson" and email "tom@example.com"
    When I update the user with name "Thomas Wilson" and email "thomas@example.com"
    Then the response status should be 200
    And the updated user should have name "Thomas Wilson"
    And the updated user should have email "thomas@example.com"

  @Regression
  Scenario: Successfully delete a user
    Given a user exists with name "Delete Me" and email "delete@example.com"
    When I delete the user
    Then the response status should be 204
    And the user should no longer exist in the database

  @Regression
  Scenario: Successfully deactivate a user
    Given a user exists with name "Active User" and email "active@example.com"
    When I deactivate the user
    Then the response status should be 200
    And the user should be inactive

  @Regression
  Scenario Outline: Validate user input
    Given I have user details with name "<name>" and email "<email>"
    When I create the user via REST API
    Then the response status should be <status>

    Examples:
      | name       | email                | status |
      | Valid User | valid@example.com    | 201    |
      |            | noemail@example.com  | 400    |
      | No Email   |                      | 400    |
      | Bad Email  | not-an-email         | 400    |

  @Performance @Slow
  Scenario: Create multiple users in sequence
    Given I want to create 5 users
    When I create all users sequentially
    Then all users should be created successfully
    And the database should contain 5 users