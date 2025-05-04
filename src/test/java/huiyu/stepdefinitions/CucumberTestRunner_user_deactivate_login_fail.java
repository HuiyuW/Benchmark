package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/user_deactivate_login_fail.feature")
@ConfigurationParameter(
  key   = Constants.GLUE_PROPERTY_NAME,
  // 扫描登录公共步骤和本场景步骤
  value = "huiyu.stepdefinitions_common, huiyu.stepdefinitions_user_deactivate_login_fail"
)
public class CucumberTestRunner_user_deactivate_login_fail { }
