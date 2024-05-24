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

        String title = "Proyectos sin ordenes de compra";

        int numRow = 0;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(title);

        Row row = sheet.createRow(numRow);
        row.createCell(1).setCellValue(title);

        numRow++;
        numRow++;

        row = sheet.createRow(numRow);
        row.createCell(1).setCellValue("Clave");
        row.createCell(2).setCellValue("Proyecto");
        row.createCell(3).setCellValue("PM");
        row.createCell(4).setCellValue("Correo");
        row.createCell(5).setCellValue("Jefe");
        row.createCell(6).setCellValue("Correo");
        row.createCell(7).setCellValue("Monto");

        numRow++;
        for( ProjectWithoutOrders p : projectWithoutOrders ) {
            row = sheet.createRow(numRow);
            row.createCell(1).setCellValue(p.getProjectKey());
            row.createCell(2).setCellValue(p.getProjectName());
            row.createCell(3).setCellValue(p.getPmName());
            row.createCell(4).setCellValue(p.getPmMail());
            row.createCell(5).setCellValue(p.getBossName());
            row.createCell(6).setCellValue(p.getBossMail());
            row.createCell(7).setCellValue(String.valueOf(p.getProjectAmount()));
        }

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
