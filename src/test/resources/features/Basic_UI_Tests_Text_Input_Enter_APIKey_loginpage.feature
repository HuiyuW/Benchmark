Feature: Basic_UI_Tests_Text_Input

  Scenario: Enter_APIKey_loginpage
    Given I open the login page "http://localhost:4200/"
    When I enter APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD" in the APIKey input field
    Then the APIKey input field should have value "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"