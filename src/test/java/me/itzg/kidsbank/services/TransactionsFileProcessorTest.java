package me.itzg.kidsbank.services;

import me.itzg.kidsbank.types.ExtendedContentTypes;
import me.itzg.kidsbank.types.Transaction;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class TransactionsFileProcessorTest {

    @Test
    public void testNormalFile() throws IOException, InvalidFormatException {
        assumeThat(System.getProperty("user.timezone"), is("UTC"));
        final ClassPathResource resource = new ClassPathResource("transactions.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            final MockMultipartFile multipartFile = new MockMultipartFile("transactions.xlsx",
                                                                          "transactions.xlsx",
                                                                          ExtendedContentTypes.XLSX,
                                                                          inputStream);

            final TransactionsFileProcessor processor = new TransactionsFileProcessor();
            final List<Transaction> transactions = processor.process(multipartFile);
            assertThat(transactions, Matchers.hasSize(12));

            assertEquals(1508081856000L, transactions.get(0).getWhen().getTime());
            assertEquals("Nonjob", transactions.get(0).getDescription());
            assertEquals(100f, transactions.get(0).getAmount(), 0.001);

            assertEquals(1506958486000L, transactions.get(11).getWhen().getTime());
            assertEquals("old thing", transactions.get(11).getDescription());
            assertEquals(-3.45f, transactions.get(11).getAmount(), 0.001);
        }
    }

    @Test
    public void testJustHeaderFile() throws IOException, InvalidFormatException {
        final ClassPathResource resource = new ClassPathResource("just-header-transactions.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            final MockMultipartFile multipartFile = new MockMultipartFile("just-header-transactions.xlsx",
                                                                          "just-header-transactions.xlsx",
                                                                          ExtendedContentTypes.XLSX,
                                                                          inputStream);

            final TransactionsFileProcessor processor = new TransactionsFileProcessor();
            final List<Transaction> transactions = processor.process(multipartFile);
            assertThat(transactions, Matchers.hasSize(0));
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySheet() throws IOException, InvalidFormatException {
        final ClassPathResource resource = new ClassPathResource("empty-sheet.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            final MockMultipartFile multipartFile = new MockMultipartFile("empty-sheet.xlsx",
                                                                          "empty-sheet.xlsx",
                                                                          ExtendedContentTypes.XLSX,
                                                                          inputStream);

            final TransactionsFileProcessor processor = new TransactionsFileProcessor();
            final List<Transaction> transactions = processor.process(multipartFile);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingDateColumn() throws IOException, InvalidFormatException {
        final ClassPathResource resource = new ClassPathResource("missing-date-column.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            final MockMultipartFile multipartFile = new MockMultipartFile("missing-date-column.xlsx",
                                                                          "missing-date-column.xlsx",
                                                                          ExtendedContentTypes.XLSX,
                                                                          inputStream);

            final TransactionsFileProcessor processor = new TransactionsFileProcessor();
            final List<Transaction> transactions = processor.process(multipartFile);
        }
    }
}