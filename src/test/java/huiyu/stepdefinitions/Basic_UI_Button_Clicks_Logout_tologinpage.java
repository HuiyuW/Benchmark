package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/Basic_UI_Button_Clicks_Logout_tologinpage.feature") 
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "huiyu.Basic_UI_Button_Clicks_Logout_tologinpage") 
public class Basic_UI_Button_Clicks_Logout_tologinpage {
}