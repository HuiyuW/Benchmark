Feature: Login to the TaaS Serviceportal

  Scenario: Successful login to the TaaS Serviceportal
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should be logged in and see my profile avatar

  Scenario: Login with incorrect password
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "1234"
    And I click the login button
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should see Username APIKey and Login Button on the login page

  Scenario: Logout after successful login
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should be logged in and see my profile avatar
    When I wait for toast error message to become invisible and profile avatar to become visible
    And I click the profile avatar
    And I click the logout button
    Then I should see Username APIKey and Login Button on the login page