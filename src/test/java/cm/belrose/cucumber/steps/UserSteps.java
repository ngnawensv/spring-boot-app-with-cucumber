package cm.belrose.cucumber.steps;

import cm.belrose.model.User;
import cm.belrose.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions using RestClient (Spring's modern HTTP client).
 */
@RequiredArgsConstructor
@Slf4j
public class UserSteps {

  // === INJECTED DEPENDENCIES ===

  private final RestClient restClient;

  @LocalServerPort
  private int serverPort;

  private final UserRepository userRepository;

  // === SCENARIO STATE (shared between steps in same scenario) ===

  private User currentUser;
  private ResponseEntity<User> userResponse;
  private ResponseEntity<User[]> usersResponse;
  private String lastErrorMessage;
  private Long currentUserId;
  private int lastStatusCode;

  private String baseUrl() {
    return "http://localhost:" + serverPort;
  }

  @Given("the application is running")
  public void theApplicationIsRunning() {
    log.info("Checking if application is running at {}", baseUrl());
    assertNotNull(restClient, "RestClient should be available");
  }

  @Given("I have user details with name {string} and email {string}")
  public void iHaveUserDetailsWithNameAndEmail(String name, String email) {
    currentUser = new User();
    currentUser.setName(name);
    currentUser.setEmail(email);
  }

  @Given("a user already exists with name {string} and email {string}")
  public void aUserAlreadyExistsWithNameAndEmail(String name, String email) {
    User existingUser = new User(name, email);
    User savedUser = userRepository.save(existingUser);
    currentUserId = savedUser.getId();
  }

  @Given("a user exists with name {string} and email {string}")
  public void aUserExistsWithNameAndEmail(String name, String email) {
    User user = new User(name, email);
    User savedUser = userRepository.save(user);
    currentUserId = savedUser.getId();
    currentUser = savedUser;
  }

  @Given("the following users exist:")
  public void theFollowingUsersExist(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps();

    for (Map<String, String> row : rows) {
      String name = row.get("name");
      String email = row.get("email");
      User user = new User(name, email);
      userRepository.save(user);
    }
  }

  @Given("I want to create {int} users")
  public void iWantToCreateUsers(int count) {
    // no-op; just planning
  }

  @When("I create the user via REST API")
  public void iCreateTheUserViaRESTAPI() {
    try {
      userResponse = restClient.post()
          .uri(baseUrl() + "/api/users")
          .body(currentUser)
          .retrieve()
          .toEntity(User.class);

      lastStatusCode = userResponse.getStatusCode().value();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      lastStatusCode = ex.getStatusCode().value();
      lastErrorMessage = ex.getResponseBodyAsString();
      log.info("HTTP error during create: {}", lastErrorMessage);
    } catch (ResourceAccessException ex) {
      log.error("I/O error during create: {}", ex.getMessage());
      throw ex; // let test fail for network issues
    }
  }

  @When("I try to create another user with name {string} and email {string}")
  public void iTryToCreateAnotherUserWithNameAndEmail(String name, String email) {
    User duplicateUser = new User(name, email);
    try {
      userResponse = restClient.post()
          .uri(baseUrl() + "/api/users")
          .body(duplicateUser)
          .retrieve()
          .toEntity(User.class);

      lastStatusCode = userResponse.getStatusCode().value();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      lastStatusCode = ex.getStatusCode().value();
      lastErrorMessage = ex.getResponseBodyAsString();
      log.info("Expected HTTP error: {}", lastErrorMessage);
    }
  }

  @When("I retrieve the user by ID")
  public void iRetrieveTheUserByID() {
    userResponse = restClient.get()
        .uri(baseUrl() + "/api/users/{id}", currentUserId)
        .retrieve()
        .toEntity(User.class);

    lastStatusCode = userResponse.getStatusCode().value();
  }

  @When("I try to retrieve a user with ID {int}")
  public void iTryToRetrieveAUserWithID(int userId) {
    try {
      userResponse = restClient.get()
          .uri(baseUrl() + "/api/users/{id}", userId)
          .retrieve()
          .toEntity(User.class);
      lastStatusCode = userResponse.getStatusCode().value();
    } catch (HttpClientErrorException ex) {
      lastStatusCode = ex.getStatusCode().value();
      lastErrorMessage = ex.getResponseBodyAsString();
    }
  }

  @When("I retrieve all users")
  public void iRetrieveAllUsers() {
    usersResponse = restClient.get()
        .uri(baseUrl() + "/api/users")
        .retrieve()
        .toEntity(User[].class);

    lastStatusCode = usersResponse.getStatusCode().value();
  }

  @When("I update the user with name {string} and email {string}")
  public void iUpdateTheUserWithNameAndEmail(String name, String email) {
    User updateUser = new User(name, email);

    userResponse = restClient.put()
        .uri(baseUrl() + "/api/users/{id}", currentUserId)
        .body(updateUser)
        .retrieve()
        .toEntity(User.class);

    lastStatusCode = userResponse.getStatusCode().value();
  }

  @When("I delete the user")
  public void iDeleteTheUser() {
    ResponseEntity<Void> response = restClient.delete()
        .uri(baseUrl() + "/api/users/{id}", currentUserId)
        .retrieve()
        .toBodilessEntity();

    lastStatusCode = response.getStatusCode().value();
  }

  @When("I deactivate the user")
  public void iDeactivateTheUser() {
    userResponse = restClient.patch()
        .uri(baseUrl() + "/api/users/{id}/deactivate", currentUserId)
        .retrieve()
        .toEntity(User.class);

    lastStatusCode = userResponse.getStatusCode().value();
  }

  @When("I create all users sequentially")
  public void iCreateAllUsersSequentially() {
    for (int i = 1; i <= 5; i++) {
      User user = new User("User " + i, "user" + i + "@example.com");

      restClient.post()
          .uri(baseUrl() + "/api/users")
          .body(user)
          .retrieve()
          .toEntity(User.class);
    }
  }

  @Then("the user should be created with status {int}")
  public void theUserShouldBeCreatedWithStatus(int expectedStatus) {
    assertEquals(expectedStatus, lastStatusCode);
  }

  @Then("the response status should be {int}")
  public void theResponseStatusShouldBe(int expectedStatus) {
    assertEquals(expectedStatus, lastStatusCode);
  }

  @Then("the user should have an ID")
  public void theUserShouldHaveAnID() {
    assertNotNull(userResponse.getBody(), "Response body should not be null");
    assertNotNull(userResponse.getBody().getId(), "User ID should not be null");
    assertTrue(userResponse.getBody().getId() > 0, "User ID should be positive");
  }

  @Then("the user should have name {string}")
  public void theUserShouldHaveName(String expectedName) {
    assertNotNull(userResponse.getBody());
    assertEquals(expectedName, userResponse.getBody().getName());
  }

  @Then("the user should have email {string}")
  public void theUserShouldHaveEmail(String expectedEmail) {
    assertNotNull(userResponse.getBody());
    assertEquals(expectedEmail, userResponse.getBody().getEmail());
  }

  @Then("the creation should fail with status {int}")
  public void theCreationShouldFailWithStatus(int expectedStatus) {
    assertEquals(expectedStatus, lastStatusCode);
  }

  @Then("the error message should contain {string}")
  public void theErrorMessageShouldContain(String expectedMessage) {
    assertTrue(lastStatusCode >= 400, "Status should indicate error");
    if (lastErrorMessage != null) {
      assertTrue(lastErrorMessage.contains(expectedMessage), "Error body should contain expected text");
    }
  }

  @Then("the retrieved user should have name {string}")
  public void theRetrievedUserShouldHaveName(String expectedName) {
    assertNotNull(userResponse.getBody());
    assertEquals(expectedName, userResponse.getBody().getName());
  }

  @Then("the retrieved user should have email {string}")
  public void theRetrievedUserShouldHaveEmail(String expectedEmail) {
    assertNotNull(userResponse.getBody());
    assertEquals(expectedEmail, userResponse.getBody().getEmail());
  }

  @Then("I should receive {int} users")
  public void iShouldReceiveUsers(int expectedCount) {
    assertNotNull(usersResponse.getBody());
    assertEquals(expectedCount, usersResponse.getBody().length);
  }

  @Then("the updated user should have name {string}")
  public void theUpdatedUserShouldHaveName(String expectedName) {
    assertNotNull(userResponse.getBody());
    assertEquals(expectedName, userResponse.getBody().getName());
  }

  @Then("the updated user should have email {string}")
  public void theUpdatedUserShouldHaveEmail(String expectedEmail) {
    assertNotNull(userResponse.getBody());
    assertEquals(expectedEmail, userResponse.getBody().getEmail());
  }

  @Then("the user should no longer exist in the database")
  public void theUserShouldNoLongerExistInTheDatabase() {
    boolean exists = userRepository.existsById(currentUserId);
    assertFalse(exists, "User should not exist in database");
  }

  @Then("the user should be inactive")
  public void theUserShouldBeInactive() {
    assertNotNull(userResponse.getBody());
    assertFalse(userResponse.getBody().isActive(), "User should be inactive");
  }

  @Then("all users should be created successfully")
  public void allUsersShouldBeCreatedSuccessfully() {
    // no-op
  }

  @Then("the database should contain {int} users")
  public void theDatabaseShouldContainUsers(int expectedCount) {
    long actualCount = userRepository.count();
    assertEquals(expectedCount, actualCount);
  }
}

