package me.itzg.kidsbank.services;

import me.itzg.kidsbank.config.KidsbankProperties;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.Kidlink;
import me.itzg.kidsbank.types.MongoIndexes;
import me.itzg.kidsbank.types.Parent;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class KidlinkServiceImplTest {

    private Parent parent;
    private Account account;

    @TestConfiguration
    @Import({KidlinkServiceImpl.class, MongoIndexes.class})
    public static class Config {
        @Bean
        public KidsbankProperties kidsbankProperties() {
            final KidsbankProperties properties = new KidsbankProperties();
            properties.setKidlinkExpiration(2);
            return properties;
        }
    }

    @MockBean
    Timestamper timestamper;

    @MockBean
    CodeGenerator codeGenerator;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    KidlinkService kidlinkService;

    @Before
    public void setUp() {
        account = new Account();
        account.setId("child1");
        account.setName("child1");
        mongoTemplate.insert(account);

        parent = new Parent();
        parent.setId("parent1");
        parent.setAccounts(Collections.singletonList(account.getId()));
        mongoTemplate.insert(parent);
    }

    @After
    public void tearDown() {
        mongoTemplate.remove(account);
        mongoTemplate.remove(parent);
    }

    @Test
    public void testShareAccount() {

        when(timestamper.now())
                .thenReturn(new Date());

        when(codeGenerator.generate(anyInt()))
                .thenReturn("1111");

        final String code = kidlinkService.shareAccount("parent1", "child1");
        assertEquals("1111", code);

        final Kidlink kidlink = kidlinkService.useCode(code);
        assertEquals(parent.getId(), kidlink.getSharedBy());
        assertEquals(account.getId(), kidlink.getAccounts().get(0));

        assertNull(kidlinkService.useCode(code));
    }

    @Test
    public void testExpirationApplied() {
        final List<IndexInfo> indexInfo = mongoTemplate.indexOps(Kidlink.class).getIndexInfo();
        assertThat(indexInfo, Matchers.hasSize(2));

        final List<String> indexedKeys = indexInfo.stream()
                .map((i) -> i.getIndexFields().get(0).getKey())
                .collect(Collectors.toList());

        assertThat(indexedKeys, Matchers.containsInAnyOrder("_id", "created"));
    }
}