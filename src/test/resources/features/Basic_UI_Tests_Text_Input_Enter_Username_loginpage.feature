Feature: Basic_UI_Tests_Text_Input

  Scenario: Enter_Username_loginpage
    Given I open the login page "http://localhost:4200/"
    When I enter username "vw2xn87" in the username input field
    Then the username input field should have value "vw2xn87"