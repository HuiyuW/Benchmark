Feature: Tenant Management

  Background:
    Given I am logged in
  Scenario: Create and verify a new Tenant
    When I select language "en"
    And I navigate to the Tenants page
    And I click the new tenant button
    And I enter tenant name "test0426"
    And I click the add target button
    And I enter target "adc"
    Then the save tenant button should be disabled
    And I click the save tenant button
    Then I should see validation error under target "This field must be a valid URI."