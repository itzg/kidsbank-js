package me.itzg.kidsbank.web;

import java.util.HashMap;
import java.util.Map;
import me.itzg.kidsbank.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Geoff Bourne
 * @since Dec 2018
 */
@Controller
public class ViewsController {

  private final TransactionsService transactionsService;

  @Autowired
  public ViewsController(TransactionsService transactionsService) {
    this.transactionsService = transactionsService;
  }

  @GetMapping(Paths.API+Paths.PARENT+"/accounts/{accountId}/export")
  public ModelAndView backupTransactions(@PathVariable String accountId) {
    Map<String, Object> model = new HashMap<>();
    model.put("transactions", transactionsService.streamAll(accountId));
    return new ModelAndView("transactions", model);
  }

}
