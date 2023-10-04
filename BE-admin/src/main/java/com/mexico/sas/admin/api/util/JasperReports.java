package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Oziel Naranjo
 * Comando para resolver problema de fuentes en Contendor
 * docker exec -u 0 api-service bash -c "apt-get update && apt-get install fontconfig ttf-dejavu -y"
 */
@Slf4j
@Component
public class JasperReports extends Utils {

    private final String KEY_REPORT = "/jaspereport/%s.jrxml";
    private final String PDF_PATH = String.format("%s%s", GeneralKeys.TMP_PATH, "/sas/%s_%s.pdf");

    public String createPdf(String report, Map<String, Object> parameters, List<?> list) throws CustomException {
        try {
            String pdfFileName = String.format(PDF_PATH, report, dateToString(new Date(), GeneralKeys.FORMAT_DATE_FOR_NAME, true));
            log.debug("Saving report to {} ...", pdfFileName);
            JasperPrint jasperPrint = createJasperPrint(report, parameters, list);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
            log.debug("Report {} created!", pdfFileName);
            return pdfFileName;
        } catch (Exception e) {
            log.error("Error building PDF report!", e);
            throw new CustomException(e.getMessage());
        }
    }

    public void buildPdf(String report, Map<String, Object> parameters, List<?> list, OutputStream outputStream) throws CustomException {
        log.debug("Building report {} to pdf", report);
        try {
            JasperPrint jasperPrint = createJasperPrint(report, parameters, list);
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            log.debug("Report build!");
        } catch (Exception e) {
            log.error("Error building PDF report!", e);
            throw new CustomException(e.getMessage());
        }
    }

    private JasperPrint createJasperPrint(String report, Map<String, Object> parameters, List<?> list) throws CustomException {
        String reportPath = String.format(KEY_REPORT, report);
        log.debug("Creating JasperPrint element of resource: {}...", reportPath);
        InputStream jrmxl = getClass().getResourceAsStream(reportPath);
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(jrmxl);
            return createJasperPrint(jasperReport, parameters);
        } catch (Exception e) {
            log.error("Error creating jasper print!", e);
            throw new CustomException(e.getMessage());
        }
    }

    private JasperPrint createJasperPrint(JasperReport jasperReport, Map<String, Object> parameters) throws CustomException {
        log.debug("Creating JasperPrint {} with {} parameters!", jasperReport.getName(), parameters.size());
        try {
            return JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        } catch (JRException e) {
            throw new CustomException(e.getMessage());
        }
    }


}