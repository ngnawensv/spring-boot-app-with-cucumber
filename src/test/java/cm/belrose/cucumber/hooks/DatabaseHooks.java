package cm.belrose.cucumber.hooks;

import cm.belrose.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Clean database before each scenario.
 * <p>
 * This ensures test isolation - each scenario starts with empty database.
 * Prevents test pollution where one scenario affects another.
 * <p>
 * Example execution:
 * 1. @Before hook runs → database cleaned
 * 2. Background steps run
 * 3. Scenario steps run
 * 4. @After hook runs
 * 5. Repeat for next scenario
 * <p>
 * Why this is important:
 * - Without cleanup, "Scenario 1" might create a user with email "test@example.com"
 * - Then "Scenario 2" tries to create the same user and fails with duplicate email
 * - This is a false negative - the test fails due to pollution, not a real bug
 */

@RequiredArgsConstructor
@Slf4j
public class DatabaseHooks {

  private final UserRepository userRepository;

  /**
   * Clean database before each scenario.
   * This ensures test isolation - each scenario starts with empty database.
   * Prevents test pollution where one scenario affects another.
   * Example execution:
   * 1. @Before hook runs → database cleaned
   * 2. Background steps run
   * 3. Scenario steps run
   * 4. @After hook runs
   * 5. Repeat for next scenario
   * Why this is important:
   * - Without cleanup, "Scenario 1" might create a user with email "test@example.com"
   * - Then "Scenario 2" tries to create the same user and fails with duplicate email
   * - This is a false negative - the test fails due to pollution, not a real bug
   */
  @Before
  public void beforeScenario() {
    log.info("=== Starting new scenario - Cleaning database ===");
    userRepository.deleteAll();
  }

  /**
   * Cleanup after scenario (optional).
   * Currently just logs, but could be used for:
   * - Additional cleanup
   * - Collecting metrics
   * - Taking screenshots (for UI tests)
   * - Closing resources
   */
  @After
  public void afterScenario() {
    log.info("=== Scenario completed ===");
  }
}
