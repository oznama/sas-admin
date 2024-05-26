package com.mexico.sas.nativequeries.api.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * docker exec -u 0 native-service bash -c "apt-get update && apt-get install fontconfig ttf-dejavu -y"
 */

@Slf4j
public class ExcelExporter {

    protected byte[] getReportByteArray(Workbook workbook) {
        // Write the workbook to a ByteArrayOutputStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Error creating report", e);
        }

        return stream.toByteArray();
    }

    protected CellStyle getTitleStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(createFont(workbook, (short) 20, true));
        return cellStyle;
    }

    protected CellStyle getTableHeaderStyle(Workbook workbook) {
        Font font = createFont(workbook, (short) 14, false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }

    protected CellStyle getTableRowStyle(Workbook workbook) {
        Font font = createFont(workbook, (short) 12, false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private Font createFont(Workbook workbook, short size, boolean bold) {
        Font font = workbook.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints(size);
        return font;
    }

    protected void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol ) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    protected void autoSizeColumn(Sheet sheet, int columns) {
        for(int i=0; i<columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
