// src/test/java/huiyu/stepdefinitions/CucumberTestRunner_logout.java
package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/logout.feature")
@ConfigurationParameter(
  key   = Constants.GLUE_PROPERTY_NAME,
  value = "huiyu.stepdefinitions_common, huiyu.stepdefinitions_logout"
)
public class CucumberTestRunner_logout { }
