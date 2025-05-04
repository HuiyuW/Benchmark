Feature: Tenant Management

  Scenario: Deactivate a user and verify that logging in again is refused
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should be logged in and see my profile avatar
    When I switch the portal language to "en"
    When I open the Administration dropdown in the top navigation
    And I select Users from the Administration dropdown
    And I locate the user "vw2xn87" in the list
    And I deactivate the user "vw2xn87"
    When I wait for toast error message to become invisible and profile avatar to become visible
    And I wait until the deactivation-confirmation toast disappears
    And I click the profile avatar
    And I click the logout button
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see Username APIKey and Login Button on the login page