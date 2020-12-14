Feature: Securing the connection between you and the world

  @Demo
  Scenario Outline: Saying hello to the Server
    Given Server is alive
    When I say hello with "<client>"
    Then I expect to receive status code 200
    And I expect to receive "Hello" message
    And I display the time it took to get the message

    Examples:
      | client                            |
      | Akka HttpClient                   |
      | Apache HttpClient                 |
      | Async HttpClient                  |
      | Dispatch Reboot HttpClient        |
      | Feign                             |
      | Finagle                           |
      | Fuel                              |
      | Google HttpClient                 |
      | Http4k Apache4 Http Client        |
      | Http4k Apache4 Async Http Client  |
      | Http4k Apache5 Http Client        |
      | Http4k Apache5 Async Http Client  |
      | Http4k Java Http Client           |
      | Http4k Jetty Http Client          |
      | Http4k OkHttp Client              |
      | Http4s Blaze Client               |
      | Http4s Java Net Client            |
      | JDK HttpClient                    |
      | Jersey HttpClient                 |
      | Jetty Reactive HttpClient         |
      | Kohttp                            |
      | Ktor Android HttpClient           |
      | Ktor Apache HttpClient            |
      | Ktor CIO HttpClient               |
      | Ktor Okhttp HttpClient            |
      | Methanol                          |
      | OkHttp                            |
      | Old JDK HttpClient                |
      | Old Jersey HttpClient             |
      | Reactor Netty                     |
      | Retrofit                          |
      | Requests Scala                    |
      | ScalaJ HttpClient                 |
      | Sttp                              |
      | Spring RestTemplate               |
      | Spring WebFlux WebClient Jetty    |
      | Spring WebFlux WebClient Netty    |
      | Unirest                           |
