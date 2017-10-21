package me.itzg.kidsbank.services;

import me.itzg.kidsbank.types.ExtendedContentTypes;
import me.itzg.kidsbank.types.Transaction;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Service
public class TransactionsFileProcessor {

    public List<Transaction> process(MultipartFile multipartFile) throws IOException, InvalidFormatException {
        final String contentType = multipartFile.getContentType();

        if (ExtendedContentTypes.XLSX.equals(contentType) ||
                multipartFile.getName().toLowerCase().endsWith(".xlsx")) {
            try (InputStream fileStream = multipartFile.getInputStream()) {
                return processXlsx(fileStream);
            }
        }

        throw new IllegalArgumentException(String.format("Unsupported file type; contentType=%s, name=%s",
                                                         contentType,
                                                         multipartFile.getName()));
    }

    private List<Transaction> processXlsx(InputStream fileStream) throws IOException, InvalidFormatException {
        final Workbook wb = WorkbookFactory.create(fileStream);

        if (wb.getNumberOfSheets() == 0) {
            throw new IllegalArgumentException("Requires at least one sheet");
        }

        final Sheet sheet = wb.getSheetAt(0);
        final int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum == 0) {
            throw new IllegalArgumentException("Requires at least a header row of Date, Description, Ammount");
        }

        int dateCol = -1;
        int descriptionCol = -1;
        int amountCol = -1;

        final Row headerRow = sheet.getRow(0);
        for (int c = headerRow.getFirstCellNum(); c < headerRow.getLastCellNum(); ++c) {
            final Cell cell = headerRow.getCell(c);
            if (cell.getStringCellValue().toLowerCase().equals("date")) {
                dateCol = c;
            } else if (cell.getStringCellValue().toLowerCase().equals("description")) {
                descriptionCol = c;
            } else if (cell.getStringCellValue().toLowerCase().equals("amount")) {
                amountCol = c;
            }
        }

        if (dateCol == -1) {
            throw new IllegalArgumentException("Missing 'Date' column");
        }
        if (descriptionCol == -1) {
            throw new IllegalArgumentException("Missing 'Description' column");
        }
        if (amountCol == -1) {
            throw new IllegalArgumentException("Missing 'Amount' column");
        }

        final ArrayList<Transaction> transactions = new ArrayList<>();

        for (int r = 1; r <= lastRowNum; ++r) {
            try {
                final Row row = sheet.getRow(r);
                final Transaction transaction = new Transaction();

                final Cell dateCell = row.getCell(dateCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (dateCell == null) {
                    throw new IllegalArgumentException(String.format("Date was missing on row %d", r + 1));
                }
                transaction.setWhen(dateCell.getDateCellValue());

                final Cell descriptionCell = row.getCell(descriptionCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (descriptionCell == null) {
                    throw new IllegalArgumentException(String.format("Description was missing on row %d", r + 1));
                }
                transaction.setDescription(descriptionCell.getStringCellValue());

                final Cell amountCell = row.getCell(amountCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (amountCell == null) {
                    throw new IllegalArgumentException(String.format("Amount was missing on row %d", r + 1));
                }
                transaction.setAmount((float) amountCell.getNumericCellValue());

                transactions.add(transaction);
            } catch (IllegalStateException e) {
                throw new IllegalArgumentException(String.format("Invalid field on row %d", r + 1), e);
            }
        }

        return transactions;
    }
}
