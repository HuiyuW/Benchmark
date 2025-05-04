package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/tenant_blank_name.feature")
@ConfigurationParameter(
  key   = Constants.GLUE_PROPERTY_NAME,
  // 扫描登录公共步骤和本场景步骤
  value = "huiyu.stepdefinitions_common, huiyu.stepdefinitions_tenant_blank_name"
)
public class CucumberTestRunner_tenant_blank_name { }
