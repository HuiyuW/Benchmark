package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/Intermediate_UI_Form_Validation_Toggle_blank_tenant_red.feature") 
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "huiyu.Intermediate_UI_Form_Validation_Toggle_blank_tenant_red") 
public class Intermediate_UI_Form_Validation_Toggle_blank_tenant_red {
}