Feature: Tenant Management

  Background:
    Given I am logged in
  Scenario: Cannot create a Tenant with empty name
    When I select language "en"
    And I navigate to the Tenants page
    And I click the new tenant button
    And I enter tenant name "test0425"
    And I clear the tenant name input field
    Then the save tenant button should be disabled
    And I click the save tenant button
    Then I should see validation error under tenant "This field is required."