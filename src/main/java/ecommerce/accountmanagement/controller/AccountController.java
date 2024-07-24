package ecommerce.accountmanagement.controller;

import ecommerce.accountmanagement.service.AccountService;
import ecommerce.accountmanagement.service.TransactionService;
import ecommerce.accountmanagement.util.UserDetailsUtil;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping(value = "/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping(value = "create-account")
    public String showCreateAccount() {
        return "create-account";
    }

    @PostMapping(value = "create-account")
    public String createAccount(@RequestParam("balance") Long balance) {
        accountService.createAccount(balance);
        return "redirect:/account/account-overview";
    }

    @GetMapping(value = "account-overview")
    public String showAccountOverview(final Model model) {
        final Long userId = UserDetailsUtil.getCurrentUserId();

        model.addAttribute("accountBalance", accountService.getAccountBalance(userId));
        model.addAttribute("transactions", transactionService.getTransactions(userId));

        return "account-overview";
    }

    @PostMapping(value = "deposit")
    public String depositAmount(@RequestParam("amount") final Long amount) {
        transactionService.depositAmount(amount);
        return "redirect:/account/account-overview";
    }

    @PostMapping(value = "withdraw")
    public String withdrawAmount(@RequestParam("amount") final Long amount) {
        transactionService.withdrawAmount(amount);
        return "redirect:/account/account-overview";
    }

    @GetMapping("/account-statistics")
    public String getStatistics(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
                                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
                                final Model model) {

        final Long userId = UserDetailsUtil.getCurrentUserId();
        model.addAttribute("totalDeposits", transactionService.getTotalDepositsForGivenUser(userId, startDate, endDate));
        model.addAttribute("totalWithdrawals", transactionService.getTotalWithdrawalsForGivenUser(userId, startDate, endDate));
        return showAccountOverview(model);
    }
}
