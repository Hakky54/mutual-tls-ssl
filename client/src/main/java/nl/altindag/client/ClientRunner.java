package nl.altindag.client;

import java.util.Arrays;
import java.util.stream.Stream;

public class ClientRunner {

    private static String[] defaultOptions = {
            "--glue", "nl.altindag.client.stepdefs",
            "--plugin", "pretty",
            "classpath:Hello.feature",
            "--strict"
    };

    public static void main(String[] args) {
        String[] cucumberOptions = Stream.of(defaultOptions, args)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

        io.cucumber.core.cli.Main.main(cucumberOptions);
    }

}
