package com.mexico.sas.admin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.security.Crypter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class CrypterTest {

    @Autowired
    Crypter crypter;

    @DisplayName("Spring test encrypt pswd with Java")
    @Test
    public void encryptPswdWithJava() throws CustomException {
        final String pswd = "12345678";
        for(int i= 0; i<10; i++) {
            String cipherPswd = crypter.encrypt(pswd);
//            System.out.println(cipherPswd);
            assertEquals(crypter.decrypt(cipherPswd), pswd);
        }
    }

    @DisplayName("Spring test decrypt pswd with Java")
    @Test
    public void validatePswdWithJava() throws CustomException {
        final String cipherPswd = crypter.decrypt("Ea6SCcSRF9rJUxhtMk3bXg==");
        String pswdCorrect = "12345678";
        String pswdWrong = "MyP@ssw0rd";
        assertEquals(cipherPswd, pswdCorrect);
        assertNotEquals(cipherPswd, pswdWrong);
    }

    @DisplayName("Spring test Base64 to Json")
    @Test
    public void testBase64ToJson() throws CustomException, JsonProcessingException {
        final StringBuffer base64Json = new StringBuffer("ewoJInByb2R1Y3QiOiB7CgkJImFwZXJ0dXJlX2NvbW1pc3Npb25fcGVyY2V");
        base64Json.append("udGFnZSI6IG51bGwsCgkJImNhdCI6ICIwLjAiLAoJCSJjb21pc2lvbiI6ICIwLjAiLAoJCSJkZXNjcmlwdGlvbiI6IC");
        base64Json.append("JGaW5hbmNpYW1pZW50byIsCgkJImZpbmFuY2luZ19pbnRlcnZhbCI6IFsKCQkJIjI0IE1lc2VzIiwKCQkJIjM2IE1lc");
        base64Json.append("2VzIiwKCQkJIjQ4IE1lc2VzIiwKCQkJIjYwIE1lc2VzIgoJCV0sCgkJImludGVyZXN0X3JhdGUiOiAiMTkuNzIiLAo");
        base64Json.append("JCSJsaWZlX2luc3VyYW5jZSI6ICIwLjAiLAoJCSJtYXhfY2F0IjogIjAuMCIsCgkJIm1heF9kb3duX3BheW1lbnRfcG");
        base64Json.append("VyY2VudGFnZSI6IG51bGwsCgkJIm1heGltdW1fYW1vdW50IjogIjgwMDAwMC4wIiwKCQkibWluX2Rvd25fcGF5bWVudC");
        base64Json.append("I6ICIwLjAiLAoJCSJtaW5fZG93bl9wYXltZW50X3BlcmNlbnRhZ2UiOiBudWxsLAoJCSJtaW5pbXVtX2Ftb3VudCI6IC");
        base64Json.append("I1MDAwLjAiLAoJCSJuYW1lIjogIkNyZWRpdG8gQXV0b21vdHJpeiIsCgkJInBlcnNvbl90eXBlIjogImZvcl9waHlzaW");
        base64Json.append("NhbF9wZXJzb24iCgl9Cn0=");

        final StringBuffer jsonString = new StringBuffer("{\n");
        jsonString.append("\t\"product\": {\n");
        jsonString.append("\t\t\"aperture_commission_percentage\": null,\n");
        jsonString.append("\t\t\"cat\": \"0.0\",\n");
        jsonString.append("\t\t\"comision\": \"0.0\",\n");
        jsonString.append("\t\t\"description\": \"Financiamiento\",\n");
        jsonString.append("\t\t\"financing_interval\": [\n");
        jsonString.append("\t\t\t\"24 Meses\",\n");
        jsonString.append("\t\t\t\"36 Meses\",\n");
        jsonString.append("\t\t\t\"48 Meses\",\n");
        jsonString.append("\t\t\t\"60 Meses\"\n");
        jsonString.append("\t\t],\n");
        jsonString.append("\t\t\"interest_rate\": \"19.72\",\n");
        jsonString.append("\t\t\"life_insurance\": \"0.0\",\n");
        jsonString.append("\t\t\"max_cat\": \"0.0\",\n");
        jsonString.append("\t\t\"max_down_payment_percentage\": null,\n");
        jsonString.append("\t\t\"maximum_amount\": \"800000.0\",\n");
        jsonString.append("\t\t\"min_down_payment\": \"0.0\",\n");
        jsonString.append("\t\t\"min_down_payment_percentage\": null,\n");
        jsonString.append("\t\t\"minimum_amount\": \"5000.0\",\n");
        jsonString.append("\t\t\"name\": \"Credito Automotriz\",\n");
        jsonString.append("\t\t\"person_type\": \"for_physical_person\"\n");
        jsonString.append("\t}\n");
        jsonString.append("}");

        String jsonStringDecode = crypter.decodeBase64(base64Json.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonExpected = objectMapper.readTree(jsonString.toString());
        JsonNode jsonActual = objectMapper.readTree(jsonStringDecode);

        assertEquals(jsonExpected, jsonActual);
    }

}
