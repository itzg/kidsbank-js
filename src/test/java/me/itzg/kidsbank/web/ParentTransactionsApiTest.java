package me.itzg.kidsbank.web;

import me.itzg.kidsbank.services.TransactionsService;
import me.itzg.kidsbank.types.Transaction;
import me.itzg.kidsbank.users.Authorities;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ParentTransactionsApi.class)
public class ParentTransactionsApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionsService transactionsService;

    @Test
    @WithMockUser(authorities = Authorities.PARENT)
    public void testTransactionCreate() throws Exception {
        Transaction created = new Transaction();
        created.setId("t-1");
        given(transactionsService.createTransaction(any(Transaction.class)))
                .willReturn(created);

        // when
        mvc.perform(post("/api/parent/accounts/{accountId}/transactions", "a-1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new JSONObject()
                                             .put("description", "desc")
                                             .put("amount", -5.0f)
                                             .put("when", 1000)
                                             .toString())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is("t-1")))
        ;

        // then
    }

    @Test
    @WithMockUser(authorities = Authorities.PARENT)
    public void testTransactionCreate_MissingDescription() throws Exception {
        Transaction created = new Transaction();
        created.setId("t-1");
        given(transactionsService.createTransaction(any(Transaction.class)))
                .willReturn(created);

        // when
        mvc.perform(post("/api/parent/accounts/{accountId}/transactions", "a-1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new JSONObject()
                                             .put("amount", -5.0f)
                                             .put("when", 1000)
                                             .toString())
        )
                .andExpect(status().isBadRequest())
        ;

        // then
    }

    @Test
    @WithMockUser(authorities = Authorities.PARENT)
    public void testTransactionCreate_IncludesId() throws Exception {
        Transaction created = new Transaction();
        created.setId("t-1");
        given(transactionsService.createTransaction(any(Transaction.class)))
                .willReturn(created);

        // when
        mvc.perform(post("/api/parent/accounts/{accountId}/transactions", "a-1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new JSONObject()
                                             .put("id", "t-X")
                                             .put("description", "desc")
                                             .put("amount", -5.0f)
                                             .put("when", 1000)
                                             .toString())
        )
                .andExpect(status().isBadRequest())
        ;

        // then
    }

    @WithMockUser(authorities = Authorities.PARENT, username = "parent-1")
    @Test
    public void testTransactionSave() throws Exception {
        Transaction saved = new Transaction();
        saved.setId("t-1");
        given(transactionsService.save(eq("parent-1"), any(Transaction.class)))
                .willReturn(saved);

        // when
        mvc.perform(put("/api/parent/transactions", "a-1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new JSONObject()
                                             .put("id", "t-1")
                                             .put("accountId", "a-1")
                                             .put("description", "desc")
                                             .put("amount", -5.0f)
                                             .put("when", 1000)
                                             .toString())
        )
                .andExpect(status().isOk())
        ;

        // then

    }
}