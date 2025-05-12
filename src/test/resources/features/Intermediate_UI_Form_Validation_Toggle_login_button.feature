Feature: Basic_UI_Button_Clicks

  Scenario: Click Logout to loginpage
    Given I open the login page "http://localhost:4200/"
    When I enter username "vw2xn87" in the username input field
    Then the login button should be disabled
    When I clear the username field
    When I enter APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD" in the APIKey input field
    Then the login button should be disabled
    When I enter username "vw2xn87" in the username input field
    Then the login button should be enabled
