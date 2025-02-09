Feature: Securing the connection between you and the world

  @Demo
  Scenario Outline: Saying hello to the Server with <client>
    Given Server is alive
    When I say hello with "<client>"
    Then I expect to receive status code 200
    And I expect to receive "Hello" message
    And I display the time it took to get the message

    Examples:
      | client                            |
      | JDK HttpClient                    |
