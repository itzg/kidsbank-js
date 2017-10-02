package me.itzg.kidsbank.services;

import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.AccountCreation;
import me.itzg.kidsbank.types.Parent;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class AccountsServiceTest {

    @TestConfiguration
    @Import(AccountsService.class)
    public static class Config {

    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccountsService accountsService;

    private Parent parent;

    @Before
    public void setUp() {
        parent = new Parent();
        mongoTemplate.save(parent);
    }

    @After
    public void tearDown() {
        mongoTemplate.remove(parent);
    }

    @Test
    public void testCreateAccount() throws NotFoundException {
        AccountCreation accountCreation = new AccountCreation();
        accountCreation.setName("child1");
        final Account account = accountsService.createAccount(parent.getId(), accountCreation);
        assertNotNull(account.getId());
    }

    @Test
    public void testGetParentManagedAccounts() throws NotFoundException {
        AccountCreation accountCreation = new AccountCreation();
        accountCreation.setName("child1");
        final Account account = accountsService.createAccount(parent.getId(), accountCreation);

        final List<Account> accounts = accountsService.getParentManagedAccounts(parent.getId());
        assertThat(accounts, Matchers.hasSize(1));
        assertEquals(account.getId(), accounts.get(0).getId());
    }

    @Test(expected = NotFoundException.class)
    public void testGetParentManagedAccountsMissingParent() throws NotFoundException {
        accountsService.getParentManagedAccounts("wrong parent ID");
    }
}