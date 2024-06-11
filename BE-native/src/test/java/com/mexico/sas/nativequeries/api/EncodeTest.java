package com.mexico.sas.nativequeries.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class EncodeTest {

    @DisplayName("Spring Test encode Special Chars")
    @Test
    public void encodeSpecialChars() {
        String wordAccented = "Oziel Naranjo Márquez";
        System.out.printf("Word Accented: %s\n", wordAccented);
    }
}
