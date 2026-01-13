package cm.belrose.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * JUnit 5 test suite runner for Cucumber tests.
 * <p>
 * This is the entry point for running Cucumber tests.
 * When you run this class (from IDE or Gradle), it:
 * 1. Starts the Spring Boot application (via CucumberSpringConfiguration)
 * 2. Discovers all .feature files
 * 3. Matches Gherkin steps to Java step definitions
 * 4. Executes scenarios
 * 5. Generates reports
 * <p>
 * Annotations explained:
 * - @Suite: Marks this as a JUnit 5 test suite
 * - @IncludeEngines("cucumber"): Use Cucumber test engine (not standard JUnit)
 * - @SelectClasspathResource("features"): Look for .feature files in src/test/resources/features/
 * <p>
 * Configuration parameters:
 * - PLUGIN_PROPERTY_NAME: Configure report generators
 *   - pretty: Console output with colors
 *   - HTML: HTML report in build/reports/cucumber/
 *   - JSON: JSON report for CI/CD tools
 * <p>
 * - GLUE_PROPERTY_NAME: Where to find step definitions
 *   - Scans com.example.demo.cucumber package and sub-packages
 *   - Finds @Given, @When, @Then methods
 * <p>
 * - FILTER_TAGS_PROPERTY_NAME: Filter which scenarios to run
 *   - "not @Ignore": Run everything except scenarios tagged with @Ignore
 *   - Can be overridden via system property: -Dcucumber.filter.tags="@Smoke"
 */

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:build/reports/cucumber/cucumber.html, json:build/reports/cucumber/cucumber.json"
)

@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "cm.belrose.cucumber")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @Ignore")
public class TestCucumberRunner {
  // This class intentionally left empty
}