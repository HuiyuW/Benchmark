Feature: Logout to the TaaS Serviceportal

  Background:
    Given I am logged in

  Scenario: Logout after successful login
    When I wait for toast error message to become invisible and profile avatar to become visible
    And I click the profile avatar
    And I click the logout button
    Then I should see Username APIKey and Login Button on the login page