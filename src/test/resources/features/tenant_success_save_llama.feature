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
    When I switch the portal language to "en"
    When I open the Administration dropdown in the top navigation
    And I select Tenants from the Administration dropdown
    And I click the New Tenant button
    And I type tenant name "test0426"
    And I click the Save Tenant button
    Then the tenant "test0426" appears in the list