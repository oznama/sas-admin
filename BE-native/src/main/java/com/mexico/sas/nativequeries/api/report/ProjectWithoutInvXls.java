package com.mexico.sas.nativequeries.api.report;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProjectWithoutInvXls extends ExcelExporter {

    public byte[] build(List<ProjectWithoutInvoices> projectWithoutInvoices) {
        log.debug("Building excel with {} projects invoices", projectWithoutInvoices.size());

        final String title = "Reporte de Proyectos sin Factura";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(title);

        int numRow = 1;
        title(sheet, numRow, workbook, title);

        numRow = headers(workbook, numRow, sheet);
        rows(projectWithoutInvoices, workbook, numRow, sheet);

        autoSizeColumn(sheet, 20);

        return getReportByteArray(workbook);
    }

    private void title(Sheet sheet, int numRow, Workbook workbook, String title) {
        log.debug("Creating title ...");
        Row row = sheet.createRow(numRow);
        Cell cellTitle = row.createCell(1);
        cellTitle.setCellStyle(getTitleStyle(workbook));
        cellTitle.setCellValue(title);
        mergeCells(sheet, numRow, numRow, 1, 7);
        log.debug("Title added!");
    }

    private int headers(Workbook workbook, int numRow, Sheet sheet) {
        log.debug("Creating headers ...");
        Row row;
        CellStyle cellStyle = getTableHeaderStyle(workbook);
        numRow++;
        numRow++;
        row = sheet.createRow(numRow);
        Cell cellHeaderKey = row.createCell(1);
        cellHeaderKey.setCellValue("Clave");
        cellHeaderKey.setCellStyle(cellStyle);
        Cell cellHeaderProject = row.createCell(2);
        cellHeaderProject.setCellValue("Proyecto");
        cellHeaderProject.setCellStyle(cellStyle);
        Cell cellHeaderPm = row.createCell(3);
        cellHeaderPm.setCellValue("PM");
        cellHeaderPm.setCellStyle(cellStyle);
        Cell cellHeaderPmMail = row.createCell(4);
        cellHeaderPmMail.setCellValue("Correo");
        cellHeaderPmMail.setCellStyle(cellStyle);
        Cell cellHeaderMount = row.createCell(5);
        cellHeaderMount.setCellValue("Monto");
        cellHeaderMount.setCellStyle(cellStyle);
        Cell cellHeaderTax = row.createCell(6);
        cellHeaderTax.setCellValue("IVA");
        cellHeaderTax.setCellStyle(cellStyle);
        Cell cellHeaderTotal = row.createCell(7);
        cellHeaderTotal.setCellValue("Total");
        cellHeaderTotal.setCellStyle(cellStyle);
        log.debug("Headers added!");
        return numRow;
    }

    private void rows(List<ProjectWithoutInvoices> projectWithoutOrders, Workbook workbook, int numRow, Sheet sheet) {
        log.debug("Creatingn rows ...");
        Row row;
        CellStyle cellStyle = getTableRowStyle(workbook);
        for( ProjectWithoutInvoices p : projectWithoutOrders) {
            numRow++;
            row = sheet.createRow(numRow);
            Cell cell01 = row.createCell(1);
            cell01.setCellValue(p.getProjectKey());
            cell01.setCellStyle(cellStyle);
            Cell cell02 = row.createCell(2);
            cell02.setCellValue(p.getProjectName());
            cell02.setCellStyle(cellStyle);
            Cell cell03 = row.createCell(3);
            cell03.setCellValue(p.getPmName());
            cell03.setCellStyle(cellStyle);
            Cell cell04 = row.createCell(4);
            cell04.setCellValue(p.getPmMail());
            cell04.setCellStyle(cellStyle);
            Cell cell05 = row.createCell(5);
            cell05.setCellValue(String.valueOf(p.getProjectAmount()));
            cell05.setCellStyle(cellStyle);
            Cell cell06 = row.createCell(6);
            cell06.setCellValue(String.valueOf(p.getTax()));
            cell06.setCellStyle(cellStyle);
            Cell cell07 = row.createCell(7);
            cell07.setCellValue(String.valueOf(p.getTotal()));
            cell07.setCellStyle(cellStyle);
            log.debug("Row {} for {} project added!", numRow, p.getProjectKey());
        }
        log.debug("Rows added!");
    }
}
