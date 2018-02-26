Feature: Todo app via WS

  Scenario: Send a TODO to the server
    When I send a TODO "clean up the house" to the server
    Then the TODO "clean up the house" was created