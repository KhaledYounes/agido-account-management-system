package ecommerce.accountmanagement.controller;

import ecommerce.accountmanagement.entity.Transaction;
import ecommerce.accountmanagement.service.AccountService;
import ecommerce.accountmanagement.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/authorize-transaction")
@AllArgsConstructor
public class AuthorizeTransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @GetMapping
    public String showAuthorizeTransaction(final Model model) {
        final List<Transaction> transactions = transactionService.getPendingTransactions();

        final List<String> accountHolderNames = transactions.stream()
                .map(Transaction::getAccountId)
                .map(accountService::getAccountHolderName)
                .toList();

        model.addAttribute("transactions", transactions);
        model.addAttribute("accountHolderNames", accountHolderNames);

        return "authorize-transaction";
    }

    @PostMapping(value = "accept")
    public String acceptTransaction(@RequestParam("transactionId") final Long transactionId) {
        transactionService.acceptTransaction(transactionId);
        return "redirect:/authorize-transaction";
    }

    @PostMapping(value = "deny")
    public String denyTransaction(@RequestParam("transactionId") final Long transactionId) {
        transactionService.denyTransaction(transactionId);
        return "redirect:/authorize-transaction";
    }
}
