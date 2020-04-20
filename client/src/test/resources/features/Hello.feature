Feature: Securing the connection between you and the world

  @Demo
  Scenario Outline: Saying hello to the Server
    Given Server is alive
    When I say hello with "<client>"
    Then I expect to receive status code 200
    And I expect to receive "Hello" message
    And I display the time it took to get the message

    Examples:
      | client                          |
      | Akka HttpClient                 |
      | Apache HttpClient               |
      | Async HttpClient                |
      | Dispatch Reboot HttpClient      |
      | Finagle                         |
      | Fuel                            |
      | Google HttpClient               |
      | JDK HttpClient                  |
      | Jersey HttpClient               |
      | Jetty Reactive HttpClient       |
      | OkHttp                          |
      | Old JDK HttpClient              |
      | Old Jersey HttpClient           |
      | Reactor Netty                   |
      | Retrofit                        |
      | ScalaJ HttpClient               |
      | Spring RestTemplate             |
      | Spring WebFlux WebClient Jetty  |
      | Spring WebFlux WebClient Netty  |
      | Unirest                         |