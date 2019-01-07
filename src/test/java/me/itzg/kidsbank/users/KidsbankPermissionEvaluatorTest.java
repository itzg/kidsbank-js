package me.itzg.kidsbank.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Collections;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.Permissions;
import me.itzg.kidsbank.types.Types;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class KidsbankPermissionEvaluatorTest {

    private Account account;
    private Parent parent;
    @Autowired
    private KidsbankPermissionEvaluator permissionEvaluator;
    @Autowired
    private MongoTemplate mongoTemplate;

    @TestConfiguration
    @Import({
        KidsbankPermissionEvaluator.class,
        ParentOAuth2DetailsLoader.class
    })
    public static class Config {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Before
    public void setUp() {
        account = new Account();
        account.setName("child1");
        mongoTemplate.save(account);

        parent = new Parent();
        parent.setAccounts(Collections.singletonList(account.getId()));
        mongoTemplate.save(parent);
    }

    @Test
    public void testParentCanShare() {
        final TestingAuthenticationToken auth = new TestingAuthenticationToken(
                parent.getId(),
                "",
                Collections.singletonList(Authorities.PARENT_AUTHORITY));
        final boolean result = permissionEvaluator.hasPermission(auth,
                                                                 account.getId(),
                                                                 Types.ACCOUNT,
                                                                 Permissions.SHARE);

        assertTrue(result);
    }

    @Test
    public void testParentDoesntExistCantShare() {
        final TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "not a real parent ID",
                "",
                Collections.singletonList(Authorities.PARENT_AUTHORITY));
        final boolean result = permissionEvaluator.hasPermission(auth,
                                                                 account.getId(),
                                                                 Types.ACCOUNT,
                                                                 Permissions.SHARE);

        assertFalse(result);
    }

    @Test
    public void testParentDoesntHaveAccountCantShare() {
        final TestingAuthenticationToken auth = new TestingAuthenticationToken(
                parent.getId(),
                "",
                Collections.singletonList(Authorities.PARENT_AUTHORITY));
        final boolean result = permissionEvaluator.hasPermission(auth,
                                                                 "wrong account ID",
                                                                 Types.ACCOUNT,
                                                                 Permissions.SHARE);

        assertFalse(result);

    }
}