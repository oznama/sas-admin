package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class UtilServiceTest {

    private Utils utils;

    @BeforeEach
    public void init() {
        utils = new Utils();
    }

    @DisplayName("Spring test date in hours valid")
    @Test
    public void dateInHoursValid() {
        final int vigencyInhours = 2;
        final int hours = 5; // Horas aumentar a fecha actual
        LocalDateTime ldtHours = LocalDateTime.now().plusHours(hours);
        Date dateHours = Date.from(ldtHours.atZone(ZoneId.systemDefault()).toInstant());
        final long lDateHours = utils.dateInHours(dateHours)*-1;
        System.out.printf("Horas\nFecha: %s\nVigencia: %d\n", lDateHours, vigencyInhours);
        assertTrue(lDateHours > vigencyInhours);
    }

    @DisplayName("Spring test date in minus valid")
    @Test
    public void dateInMinutesValid() {
        final int vigencyInMinutes = 2;
        final int minutes = 5; // Minutos aumentar a fecha actual
        LocalDateTime ldtMinutes = LocalDateTime.now().plusMinutes(minutes);
        Date dateMinutes = Date.from(ldtMinutes.atZone(ZoneId.systemDefault()).toInstant());
        final long lDateMinutes = utils.dateInMinutes(dateMinutes)*-1;
        System.out.printf("Minutos\nFecha: %s\nVigencia: %d\n", lDateMinutes, vigencyInMinutes);
        assertTrue(lDateMinutes > vigencyInMinutes);
    }

    @DisplayName("Spring test date in seconds valid")
    @Test
    public void dateInSecondsValid() {
        final int vigencyInSeconds = 30;
        final int seconds = 45; // Segundos a restar a fecha actual
        LocalDateTime ldtSeconds = LocalDateTime.now().plusSeconds(seconds);
        Date dateSeconds = Date.from(ldtSeconds.atZone(ZoneId.systemDefault()).toInstant());
        long lDateSeconds = utils.dateInSeconds(dateSeconds)*-1;
        System.out.printf("Segundos\nFecha: %s\nVigencia: %d\n", lDateSeconds, vigencyInSeconds);
        assertTrue(lDateSeconds > vigencyInSeconds);
    }
}
