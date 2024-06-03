package com.mexico.sas.nativequeries.api.report;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProjectWithoutInvXls extends ExcelExporter {

    public byte[] build(List<ProjectWithoutInvoices> projectWithoutInvoices, Boolean orderCanceled) {
        log.debug("Building excel with {} projects invoices", projectWithoutInvoices.size());

        final String title = "Reporte de Proyectos sin Factura";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(title);

        int numRow = 1;
        title(sheet, numRow, workbook, title, orderCanceled);

        numRow = headers(workbook, numRow, sheet, orderCanceled);
        rows(projectWithoutInvoices, workbook, numRow, sheet, orderCanceled);

        autoSizeColumn(sheet, 20);

        return getReportByteArray(workbook);
    }

    private void title(Sheet sheet, int numRow, Workbook workbook, String title, Boolean orderCanceled) {
        log.debug("Creating title ...");
        Row row = sheet.createRow(numRow);
        Cell cellTitle = row.createCell(1);
        cellTitle.setCellStyle(getTitleStyle(workbook));
        cellTitle.setCellValue(title);
        mergeCells(sheet, numRow, numRow, 1, orderCanceled ? 7 : 8);
        log.debug("Title added!");
    }

    private int headers(Workbook workbook, int numRow, Sheet sheet, Boolean orderCanceled) {
        log.debug("Creating headers ...");
        Row row;
        CellStyle cellStyle = getTableHeaderStyle(workbook);
        numRow++;
        numRow++;
        int numColumn = 1;
        row = sheet.createRow(numRow);
        Cell cellHeaderKey = row.createCell(numColumn++);
        cellHeaderKey.setCellValue("Clave");
        cellHeaderKey.setCellStyle(cellStyle);
        Cell cellHeaderProject = row.createCell(numColumn++);
        cellHeaderProject.setCellValue("Proyecto");
        cellHeaderProject.setCellStyle(cellStyle);
        Cell cellHeaderOrderNum = row.createCell(numColumn++);
        cellHeaderOrderNum.setCellValue("Orden");
        cellHeaderOrderNum.setCellStyle(cellStyle);
        Cell cellHeaderPm = row.createCell(numColumn++);
        cellHeaderPm.setCellValue("PM");
        cellHeaderPm.setCellStyle(cellStyle);
        Cell cellHeaderPmMail = row.createCell(numColumn++);
        cellHeaderPmMail.setCellValue("Correo");
        cellHeaderPmMail.setCellStyle(cellStyle);
        Cell cellHeaderMount = row.createCell(numColumn++);
        cellHeaderMount.setCellValue("Monto");
        cellHeaderMount.setCellStyle(cellStyle);
        Cell cellHeaderTax = row.createCell(numColumn++);
        cellHeaderTax.setCellValue("IVA");
        cellHeaderTax.setCellStyle(cellStyle);
        Cell cellHeaderTotal = row.createCell(numColumn++);
        cellHeaderTotal.setCellValue("Total");
        cellHeaderTotal.setCellStyle(cellStyle);
        if( !orderCanceled ) {
            Cell cellHeaderStage = row.createCell(numColumn);
            cellHeaderStage.setCellValue("Etapa");
            cellHeaderStage.setCellStyle(cellStyle);
        }
        log.debug("Headers added!");
        return numRow;
    }

    private void rows(List<ProjectWithoutInvoices> projectWithoutOrders, Workbook workbook, int numRow, Sheet sheet, Boolean orderCanceled) {
        log.debug("Creatingn rows ...");
        Row row;
        CellStyle cellStyle = getTableRowStyle(workbook);
        for( ProjectWithoutInvoices p : projectWithoutOrders) {
            numRow++;
            int numColumn = 1;
            row = sheet.createRow(numRow);
            Cell cell01 = row.createCell(numColumn++);
            cell01.setCellValue(p.getProjectKey());
            cell01.setCellStyle(cellStyle);
            Cell cell02 = row.createCell(numColumn++);
            cell02.setCellValue(p.getProjectName());
            cell02.setCellStyle(cellStyle);
            Cell cell03 = row.createCell(numColumn++);
            cell03.setCellValue(p.getOrderNum());
            cell03.setCellStyle(cellStyle);
            Cell cell04 = row.createCell(numColumn++);
            cell04.setCellValue(p.getPmName());
            cell04.setCellStyle(cellStyle);
            Cell cell05 = row.createCell(numColumn++);
            cell05.setCellValue(p.getPmMail());
            cell05.setCellStyle(cellStyle);
            Cell cell06 = row.createCell(numColumn++);
            cell06.setCellValue(String.valueOf(p.getProjectAmount()));
            cell06.setCellStyle(cellStyle);
            Cell cell07 = row.createCell(numColumn++);
            cell07.setCellValue(String.valueOf(p.getTax()));
            cell07.setCellStyle(cellStyle);
            Cell cell08 = row.createCell(numColumn++);
            cell08.setCellValue(String.valueOf(p.getTotal()));
            cell08.setCellStyle(cellStyle);
            if( !orderCanceled ) {
                Cell cell09 = row.createCell(numColumn);
                cell09.setCellValue(getStage(p.getPercentage()));
                cell09.setCellStyle(cellStyle);
            }
            log.debug("Row {} for {} project added!", numRow, p.getProjectKey());
        }
        log.debug("Rows added!");
    }

    private String getStage(Integer percentage) {
        return percentage == 30 ? "Construcción" : (
                percentage == 60 ? "Instalación" : (
                        percentage == 100 ? "Monitoreo" : ""
                        )
                );
    }
}
