Feature: Securing the connection between you and the world

  @Demo
  Scenario: Saying hello to the Server
    Given Server is alive
    When I say hello
    Then I expect to receive status code 200
    And I expect to receive Hello message