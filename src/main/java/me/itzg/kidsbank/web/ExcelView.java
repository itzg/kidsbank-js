package me.itzg.kidsbank.web;

import me.itzg.kidsbank.types.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.util.CloseableIterator;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class ExcelView extends AbstractXlsxView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest httpServletRequest,
                                      HttpServletResponse response) throws Exception {
        final Sheet sheet = workbook.createSheet("Transactions");
        sheet.setDefaultColumnWidth(15);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        CellStyle dateStyle = workbook.createCellStyle();
        final DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();
        dateStyle.setDataFormat(dataFormat.getFormat("m/d/yyyy"));
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat((short) 7);

        final Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.createCell(1).setCellValue("Description");
        headerRow.getCell(1).setCellStyle(headerStyle);
        headerRow.createCell(2).setCellValue("Amount");
        headerRow.getCell(2).setCellStyle(headerStyle);

        int r = 1;
        try (CloseableIterator<Transaction> transactions = (CloseableIterator<Transaction>) model.get("transactions")) {
            while (transactions.hasNext()) {
                final Transaction transaction = transactions.next();
                final Row row = sheet.createRow(r);
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(transaction.getWhen());
                dateCell.setCellStyle(dateStyle);
                final Cell descCell = row.createCell(1);
                descCell.setCellValue(transaction.getDescription());
                final Cell amountCell = row.createCell(2);
                amountCell.setCellValue(transaction.getAmount());
                amountCell.setCellStyle(currencyStyle);
                ++r;
            }
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.xlsx\"");
    }
}
