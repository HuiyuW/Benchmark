Feature: Tenant Management

  Scenario: Create and verify a new Tenant
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should be logged in and see my profile avatar 
    When I select language "en"
    And I navigate to the Tenants page
    And I click the new tenant button
    And I enter tenant name "test0426"
    And I click the add target button
    And I enter target "adc"
    Then the save tenant button should be disabled
    And I click the save tenant button
    Then I should see validation error under target "This field must be a valid URI."