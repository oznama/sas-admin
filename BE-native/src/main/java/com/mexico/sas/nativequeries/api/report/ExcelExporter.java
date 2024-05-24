package com.mexico.sas.nativequeries.api.report;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ExcelExporter {

    public byte[] build(List<ProjectWithoutOrders> projectWithoutOrders) {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Proyectos sin ordenes de compra");

        Row row = sheet.createRow(0);

        row.createCell(0).setCellValue("Gerar");
        row.createCell(1).setCellValue("De Jesus");
        row = sheet.createRow(1);
        row.createCell(0).setCellValue("Arellano");
        row.createCell(1).setCellValue(30);

        // Write the workbook to a ByteArrayOutputStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Error creating report", e);
        }

        return stream.toByteArray();
    }
}
