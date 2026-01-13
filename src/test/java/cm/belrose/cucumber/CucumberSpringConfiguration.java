package cm.belrose.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

/**
 * Cucumber-Spring integration configuration.
 * <p>
 * This class bridges Cucumber and Spring Boot testing framework.
 * <p>
 * Annotations explained:
 * - @CucumberContextConfiguration: Tells Cucumber this is THE Spring configuration class
 *   (Only ONE class in your project should have this annotation)
 * <p>
 * - @SpringBootTest: Starts the full Spring Boot application context for testing
 *   - webEnvironment = RANDOM_PORT: Starts embedded server on random available port
 *     This prevents port conflicts when running multiple test suites
 * <p>
 * - @ActiveProfiles("test"): Activates the "test" profile
 *   This loads application-test.yml configuration
 * <p>
 * Key differences from production:
 * - Random port instead of 8080
 * - Test database (H2 in-memory)
 * - Test-specific configurations
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

  /** Injected random port number of the embedded server.
   * Can be used in step definitions to construct base URL for REST calls.
   */
  @TestConfiguration
  static class RestClientTestConfiguration {

    @Bean
    public RestClient restClient() {
      return RestClient.builder()
          .build();
    }
  }
}
