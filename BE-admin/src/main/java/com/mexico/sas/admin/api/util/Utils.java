package com.mexico.sas.admin.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.ResponseErrorDetailDto;
import com.mexico.sas.admin.api.dto.UserDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.ValidationRequestException;
import com.mexico.sas.admin.api.security.AuthorizationFilter;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Oziel Naranjo
 */
@Slf4j
public class Utils extends LogMovementUtils {

    /**
     * Parsing objects with BeanUtils copyPropertoes
     * @param from object
     * @param to object
     * @param <M> From class
     * @param <N> To class
     * @return to object set
     * @throws CustomException catch any possible exception
     */
    @SuppressWarnings("deprecation")
    protected <M, N> N from_M_To_N(M from, Class<N> to) throws CustomException {
//        log.debug("from_M_To_N...");
        try {
            if (from == null)
                throw new NullPointerException();
            N objectTo = to.newInstance();
            BeanUtils.copyProperties(from, objectTo);
            return objectTo;
        } catch (Exception e) {
            String ERROR_PARSING = "Error parsing to %s";
            String msgError = String.format(ERROR_PARSING, to.getSimpleName());
            log.error(msgError, e);
            throw new CustomException(msgError);
        }
    }

    public long dateInHours(Date date) {
        log.debug("Date in hours");
        return getDurationBetweenNow(date).toHours();
    }

    public long dateInMinutes(Date date) {
        log.debug("Date in minutes");
        return getDurationBetweenNow(date).toMinutes();
    }

    public long dateInSeconds(Date date) {
        log.debug("Date in seconds");
        return getDurationBetweenNow(date).getSeconds();
    }



    private Duration getDurationBetweenNow(Date date) {
        log.debug("Getting beetween {} and now", date);
        try {
            return Duration.between(
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    LocalDateTime.now());
        } catch (Exception e) {
            log.error("Impossible get durations of {} date, error: {}", date, e.getMessage());
            return Duration.ZERO;
        }
    }

    protected long getMonthsBetweenDates(Date startDate, Date endDate) {
        return ChronoUnit.MONTHS.between(
                Instant.ofEpochMilli(startDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1),
                Instant.ofEpochMilli(endDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1));
    }

    protected boolean validDays(Date dateInit, Date dateEnd) {
        Calendar calendarInit = Calendar.getInstance(AuthorizationFilter.LOCALE);
        Calendar calendarEnd = Calendar.getInstance(AuthorizationFilter.LOCALE);
        calendarInit.setTime(dateInit);
        calendarEnd.setTime(dateEnd);
        int dayInit = calendarInit.get(Calendar.DAY_OF_MONTH);
        int dayEnd = calendarEnd.get(Calendar.DAY_OF_MONTH);
        return dayInit >= dayEnd;
    }

    public Date stringToDate(String date, String pattern) throws CustomException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            return sdf.parse(date);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    protected LocalDate dateToLocalDate(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    protected Date dateNowSummed(int toAdd, boolean isSum) {
        LocalDate now = LocalDate.now();
        now = isSum ? now.plusMonths(toAdd) : now.minusMonths(toAdd);
        return Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected String dateToString(Date date, String pattern, boolean withLocale) throws CustomException {
        try {
            SimpleDateFormat sdf = withLocale ? new SimpleDateFormat(pattern, AuthorizationFilter.LOCALE) :
                    new SimpleDateFormat(pattern);
            return sdf.format(date);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    protected double getRateInPorcentage(double rate) {
        return rate / GeneralKeys.PERC_100;
    }

    protected double doubleScale(int scale, double d, boolean isUp, boolean presiced) {
        return new BigDecimal(d)
                .setScale(scale, isUp ? ( presiced ? RoundingMode.HALF_UP : RoundingMode.UP) : RoundingMode.DOWN)
                .doubleValue();
    }

    public static double doubleScale(double d) {
        final DecimalFormat dfHu = new DecimalFormat("0.00");
        return Double.parseDouble(dfHu.format(d));
    }

    protected String doubleToStringScale(Double d, String format) {
        if(d == null) return null;
        return new DecimalFormat(format).format(d);
    }

    protected String formatCurrency(double d) {
        return NumberFormat.getCurrencyInstance(AuthorizationFilter.LOCALE).format(d);
    }

    protected String formatPercent(double d) {
        NumberFormat numberFormat = NumberFormat.getPercentInstance(AuthorizationFilter.LOCALE);
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(d);
    }

    protected List<File> buildFilesAttachment(List<String> fileNames) {
        List<File> files = new ArrayList<>();
        if(fileNames != null && !fileNames.isEmpty())
            fileNames.forEach( fileName -> files.add(new File(fileName)));
        return files;
    }

    protected String buildFullname(String name, String secondName, String surname, String secondSurname) {
        secondName = StringUtils.isEmpty(secondName) ? "" : " " + secondName;
        secondSurname = StringUtils.isEmpty(secondSurname) ? "" : " " + secondSurname;
        return String.format("%s%s %s%s", name, secondName, surname, secondSurname);
    }

    protected String buildFullname(String name, String secondName) {
        secondName = StringUtils.isEmpty(secondName) ? "" : " " + secondName;
        return String.format("%s%s", name, secondName);
    }

    protected String buildDateWithFormatDD_MMMM_YYYY(Date date, String separator) throws CustomException {
        if(date == null) date = new Date();
        String day = dateToString(date, "d", true);
        String month = dateToString(date, "MMMM", true);
        String year = dateToString(date, "yyyy", true);
        if(StringUtils.isEmpty(separator)) {
            separator = I18nResolver.getMessage(I18nKeys.GENERAL_WORD_OF);
        } else {
            month = String.format("%s%s",
                    month.substring(0,1).toUpperCase(AuthorizationFilter.LOCALE),
                    month.substring(1));
        }
        separator = StringUtils.isEmpty(separator) ? separator : String.format(" %s", separator);
        return String.format("%s%s %s%s %s", day, separator, month, separator, year);
    }

    protected <T> T stringToJson(String jsonString, Class<T> clazz) throws CustomException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error to parse string to json", e);
            throw new CustomException(e.getMessage());
        }
    }

    public String downloadFile(String url) {
        try {
            final String pathLogoTemp = String.format("%s%s", GeneralKeys.TMP_PATH, url.substring(url.lastIndexOf(GeneralKeys.DIAGONAL)));
            final URL source = new URL(url);
            URLConnection urlConnection = source.openConnection();
            urlConnection.setRequestProperty("User-Agent", GeneralKeys.REQUEST_PROPERTY_USER_AGENT);
            InputStream is = urlConnection.getInputStream();
            OutputStream os = new FileOutputStream(pathLogoTemp);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) os.write(b, 0, length);
            is.close();
            os.close();
            log.debug("File {} download to {}", url, pathLogoTemp);
            return pathLogoTemp;
        } catch (Exception e) {
            String msgError = String.format("Error to download file from %s", url);
            log.error(msgError, e);
            return null;
        }
    }

    public void deleteFile(String filePath) {
        if(!StringUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = FileUtils.deleteQuietly(file);
                log.debug("File {} deleted? {}", filePath, deleted);
            }
        }
    }

    protected Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    protected UserDto getCurrentUser() {
        log.debug("Getting CURRENT USER ID In security context ...!");
        UserDto userDto = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            userDto = (UserDto) securityContext.getAuthentication().getPrincipal();
            log.debug("UserId get: {}", userDto.getId());
            return userDto;
        } catch (NullPointerException e) {
            log.warn("Problem to get security context because is null");
        } catch (Exception e) {
            log.error("Error to get security context", e);
        }

        // Only for test
        userDto = new UserDto();
        userDto.setId(GeneralKeys.ROOT_USER_ID);
        return userDto;
    }

    protected void validationDates(String startDate, String endDate, String pattern) throws ValidationRequestException {
        List<ResponseErrorDetailDto> errors = new ArrayList<>();
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            try {
                Date iDate = stringToDate(startDate, pattern);
                Date eDate = stringToDate(endDate, pattern);
                if (iDate != null && eDate != null) {
                    boolean datesCorects = iDate.before(eDate) || iDate.equals(eDate);
                    long diffDates = getMonthsBetweenDates(iDate, eDate);
                    boolean dateInMonth = diffDates == GeneralKeys.REPORT_PERIOD_MONTH_VALID ? validDays(iDate, eDate) :
                            (diffDates < GeneralKeys.REPORT_PERIOD_MONTH_VALID ? true : false);
                    if (!(datesCorects && dateInMonth)) {
                        errors.add(new ResponseErrorDetailDto(I18nResolver.getMessage(I18nKeys.LABEL_DATE_PARAM), I18nResolver.getMessage(I18nKeys.VALIDATION_VALUE_INVALID)));
                    }
                }
            } catch (CustomException e) {
                errors.add(new ResponseErrorDetailDto(I18nResolver.getMessage(I18nKeys.LABEL_DATE_PARAM), e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationRequestException(errors);
        }
    }

    protected String normalize(String input) {

        if(!StringUtils.hasText(input)) return null;

        input = input.replaceAll("[\u00e0\u00e1\u00e2\u00e3\u00e4\u00e5]","a");
        input = input.replaceAll("[\u00e8\u00e9\u00ea\u00eb]","e");
        input = input.replaceAll("[\u00ec\u00ed\u00ee\u00ef]","i");
        input = input.replaceAll("[\u00f2\u00f3\u00f4\u00f5\u00f6]","o");
        input = input.replaceAll("[\u00f9\u00fa\u00fb\u00fc]","u");

        input = input.replaceAll("[\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5]","A");
        input = input.replaceAll("[\u00c8\u00c9\u00ca\u00cb]","E");
        input = input.replaceAll("[\u00cc\u00cd\u00ce\u00cf]","I");
        input = input.replaceAll("[\u00d2\u00d3\u00d4\u00d5\u00d6]","O");
        input = input.replaceAll("[\u00d9\u00da\u00db\u00dc]","U");

        return input;
    }

}
