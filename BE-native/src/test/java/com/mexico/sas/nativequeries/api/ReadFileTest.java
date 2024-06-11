package com.mexico.sas.nativequeries.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class ReadFileTest {

    @DisplayName("Spring Test read file name")
    @Test
    public void readFileName() {
        File file = new File("/home/xpg/Documentos/F-SIS-42 Especificación_Técnica_C-24-1643-23_V1.0.docx");
        System.out.printf("File Name: %s\n", file.getName());
    }
}
