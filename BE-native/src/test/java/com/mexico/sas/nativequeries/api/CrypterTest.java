package com.mexico.sas.nativequeries.api;

import com.mexico.sas.nativequeries.api.crypt.Crypter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class CrypterTest {

    @Autowired
    Crypter crypter;

    @DisplayName("Spring test decrypt word")
    @Test
    public void decryptWord() throws Exception {
        final String cipherPswd = crypter.decrypt("Ea6SCcSRF9rJUxhtMk3bXg==");
        System.out.printf("cipherPswd: %s\n", cipherPswd);
        String pswdCorrect = "12345678";
        String pswdWrong = "MyP@ssw0rd";
        assertEquals(cipherPswd, pswdCorrect);
        assertNotEquals(cipherPswd, pswdWrong);
    }

    @DisplayName("Spring test encrypt word")
    @Test
    public void encryptWord() throws Exception {
        final String cipherPswd = crypter.encrypt("Sas2022");
        System.out.printf("cipherPswd: %s\n", cipherPswd);
    }

}
