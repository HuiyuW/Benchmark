Feature: Basic_UI_Button_Clicks

  Scenario: Click Logout to loginpage
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    When I wait for toast error message to become invisible and profile avatar to become visible
    When I click the language dropdown to expand it
    When I select "English" from the dropdown
    Then the page title should asynchronously update to "Welcome to the TaaS selfservice portal."


  Scenario: Click Logout to loginpage
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    When I wait for toast error message to become invisible and profile avatar to become visible
    When I click the language dropdown to expand it
    When I select "Deutsch" from the dropdown
    Then the page title should asynchronously update to "Willkommen auf dem TaaS Serviceportal."
