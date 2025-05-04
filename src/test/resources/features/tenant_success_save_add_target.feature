Feature: Tenant Management

  Background:
    Given I am logged in
  Scenario: Create and verify a new Tenant
    When I select language "en"
    And I navigate to the Tenants page
    And I click the new tenant button
    And I enter tenant name "test0426"
    And I click the add target button
    And I enter target "https://taas.pre.eu.dp.vwg-connect.com/"
    And I click the save tenant button
    Then I should see tenant "test0426" in the list