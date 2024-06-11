package com.mexico.sas.admin.api.security;

import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

/**
 * @author Oziel Naranjo
 * Reference: https://www.javaguides.net/2020/02/java-string-encryption-decryption-example.html
 */
@Component
@Slf4j
public class Crypter {

    @Value("${api.security.cipher-secret}")
    private String secret;

    @Value("${api.security.cipher-secret-ft}")
    private String secretFt;

    private SecretKeySpec secretKey;
    private byte[] key;
    private final String ALGORITHM = "AES";

    /**
     *
     * @throws CustomException
     */
    public void prepareSecretKey(String secret) throws CustomException {
        try {
            key = secret.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error creating Secret Key", e);
            throw new CustomException(e.getMessage());
        }
    }

    /**
     *
     * @param strToEncrypt
     * @return
     * @throws CustomException
     */
    public String encrypt(String strToEncrypt, int origin) throws CustomException {
        prepareSecretKey(origin == 1 ? secretFt : secret);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Error to encrypt string {}, error: {}", strToEncrypt, e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     *
     * @param strToDecrypt
     * @return
     * @throws CustomException
     */
    public String decrypt(String strToDecrypt, int origin) throws CustomException {
        if(StringUtils.isEmpty(strToDecrypt))
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.VALIDATION_PASSWORD_REQUIRED), null);
        prepareSecretKey(origin == 1 ? secretFt : secret);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error to decrypt string {}, error: {}", strToDecrypt, e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     *
     * @param publicKey
     * @param privateKey
     * @param dateIn
     * @return
     * @throws CustomException
     */
    public String generateSignurate(String publicKey, String privateKey, Date dateIn) throws CustomException {
        try {
            Date now = new Date();
            Date dateToUse = dateIn != null ? dateIn : now;
            Long dateInt = dateToUse.getTime();
            String nowIntStr = String.valueOf( dateInt ).substring(0, 9);
            String publicKeyDate = String.format("%s%s", publicKey, nowIntStr);
            String signature = sha1(publicKeyDate, privateKey);
            return signature;
        } catch (IndexOutOfBoundsException e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     *
     * @param string
     * @param key
     * @return
     * @throws CustomException
     */
    private String sha1(String string, String key) throws CustomException {
        final String algorithm = "HmacSHA1";
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            return bytesToHex(mac.doFinal(string.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error SHA1 to string {}, error: {}", string, e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     *
     * @param hash
     * @return
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String decodeBase64(String stringBase64) throws CustomException {
        log.debug("Decoding string: {}", stringBase64.length()>= 20 ? stringBase64.substring(0,19) : stringBase64);
        try {
            byte[] decodeBytes = Base64.getDecoder().decode(stringBase64);
            return new String(decodeBytes);
        } catch (Exception e) {
            log.error("Error decoding string", e);
            throw new CustomException(e.getMessage());
        }
    }

}
