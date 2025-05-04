Feature: Tenant Management

  Background:
    Given I am logged in
  Scenario: Deactivate a user and verify re-login fails
    When I select language "en"
    When I click the Users navigation
    And I see user "vw2xn87"
    And I deactivate user "vw2xn87"
    When I wait for toast error message to become invisible and profile avatar to become visible
    And I wait for deactivate confirmation toast to disappear
    And I click the profile avatar
    And I click the logout button
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see Username APIKey and Login Button on the login page