package nl.altindag.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ConstantsShould {

    @Test
    public void returnClientType() {
        assertThat(Constants.HEADER_KEY_CLIENT_TYPE).isEqualTo(TestConstants.HEADER_KEY_CLIENT_TYPE);
    }

}
