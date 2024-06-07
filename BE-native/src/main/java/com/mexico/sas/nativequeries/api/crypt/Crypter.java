package com.mexico.sas.nativequeries.api.crypt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author Oziel Naranjo
 * Reference: https://www.javaguides.net/2020/02/java-string-encryption-decryption-example.html
 */
@Component
@Slf4j
public class Crypter {

    @Value("${api.security.cipher-secret}")
    private String secret;

    private SecretKeySpec secretKey;
    private byte[] key;
    private final String ALGORITHM = "AES";

    public String encrypt(String strToEncrypt) throws Exception {
        prepareSecretKey();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Error to encrypt string {}, error: {}", strToEncrypt, e.getMessage());
            throw e;
        }
    }

    public String decrypt(String strToDecrypt) throws Exception {
        if(StringUtils.isEmpty(strToDecrypt))
            throw new Exception("string empty");
        prepareSecretKey();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error to decrypt string {}, error: {}", strToDecrypt, e.getMessage());
            throw e;
        }
    }

    private void prepareSecretKey() throws Exception {
        try {
            key = secret.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            log.error("Error creating Secret Key", e);
            throw e;
        }
    }

}
