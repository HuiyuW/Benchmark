package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/user_deactivate_admin.feature") 
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "huiyu.stepdefinitions_user_deactivate_admin") 
public class CucumberTestRunner_user_deactivate_admin {
}