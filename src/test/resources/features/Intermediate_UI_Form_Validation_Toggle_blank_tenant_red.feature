Feature: Basic_UI_Button_Clicks

  Scenario: Click Logout to loginpage
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    When I hover over the Administration menu
    When I click the "Tenant" option in the Administration dropdown
    And I click the new tenant button
    And I click the tenant name input field
    And I click the save tenant button
    Then I should see validation error under tenant "This field is required."

