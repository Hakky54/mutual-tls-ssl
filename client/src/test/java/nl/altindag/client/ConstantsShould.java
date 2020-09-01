package nl.altindag.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ConstantsShould {

    @Test
    void returnClientType() {
        assertThat(Constants.HEADER_KEY_CLIENT_TYPE).isEqualTo(TestConstants.HEADER_KEY_CLIENT_TYPE);
    }

}
