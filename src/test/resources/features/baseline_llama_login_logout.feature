Feature: Login to the TaaS Service Portal

  Scenario: Successful login to the TaaS Service Portal
    Given I open the login page at "http://localhost:4200/"
    Then I should see the Username field, APIKey field, and a Login button
    When I enter the username "vw2xn87" and the APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the Terms and Conditions popup
    When I scroll or move to the "Accept" button to ensure it is visible
    And I click the "Accept" button
    Then I should be successfully logged in and see my profile avatar

  Scenario: Login with incorrect APIKey
    Given I open the login page at "http://localhost:4200/"
    Then I should see the Username field, APIKey field, and a Login button
    When I enter the username "vw2xn87" and the APIKey "1234"
    And I click the login button
    When I scroll or move to the "Accept" button to ensure it is visible
    And I click the "Accept" button
    Then I should return to the login page with Username field, APIKey field, and a Login button

  Scenario: Logout after successful login
    Given I open the login page at "http://localhost:4200/"
    Then I should see the Username field, APIKey field, and a Login button
    When I enter the username "vw2xn87" and the APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the Terms and Conditions popup
    When I scroll or move to the "Accept" button to ensure it is visible
    And I click the "Accept" button
    Then I should be successfully logged in and see my profile avatar
    When I wait for any toast error message to disappear and the profile avatar to become visible
    And I click the profile avatar
    And I click the "Logout" button
    Then I should return to the login page with Username field, APIKey field, and a Login button