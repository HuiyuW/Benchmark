Feature: Login to the TaaS Serviceportal

  Scenario: Login with incorrect password
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "1234"
    And I click the login button
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should see Username APIKey and Login Button on the login page