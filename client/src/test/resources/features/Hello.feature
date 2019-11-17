Feature: Securing the connection between you and the world

  @Demo
  Scenario Outline: Saying hello to the Server
    Given Server is alive
    When I say hello with <client>
    Then I expect to receive status code 200
    And I expect to receive Hello message

    Examples:
      | client                          |
      | Apache HttpClient               |
      | JDK HttpClient                  |
      | Old JDK HttpClient              |
      | Spring RestTemplate             |
      | Spring WebFlux WebClient Netty  |
      | Spring WebFlux WebClient Jetty  |
      | OkHttp                          |
      | Jersey Client                   |
      | Old Jersey Client               |
      | Google HttpClient               |
      | Unirest                         |
