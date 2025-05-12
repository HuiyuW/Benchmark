package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/Basic_UI_Tests_Text_Input_Enter_APIKey_loginpage.feature") 
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "huiyu.Basic_UI_Tests_Text_Input_Enter_APIKey_loginpage") 
public class Basic_UI_Tests_Text_Input_Enter_APIKey_loginpage {
}