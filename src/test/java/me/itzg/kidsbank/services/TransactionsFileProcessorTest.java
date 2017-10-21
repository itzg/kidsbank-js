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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class TransactionsFileProcessorTest {

    @Test
    public void testNormalFile() throws IOException, InvalidFormatException {
        final ClassPathResource resource = new ClassPathResource("transactions.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            final MockMultipartFile multipartFile = new MockMultipartFile("transactions.xlsx",
                                                                          "transactions.xlsx",
                                                                          ExtendedContentTypes.XLSX,
                                                                          inputStream);

            final TransactionsFileProcessor processor = new TransactionsFileProcessor();
            final List<Transaction> transactions = processor.process(multipartFile);
            assertThat(transactions, Matchers.hasSize(12));

            assertEquals(1508099856000L, transactions.get(0).getWhen().getTime());
            assertEquals("Nonjob", transactions.get(0).getDescription());
            assertEquals(100f, transactions.get(0).getAmount(), 0.001);

            assertEquals(1506976486000L, transactions.get(11).getWhen().getTime());
            assertEquals("old thing", transactions.get(11).getDescription());
            assertEquals(-3.45f, transactions.get(11).getAmount(), 0.001);
        }
    }
}