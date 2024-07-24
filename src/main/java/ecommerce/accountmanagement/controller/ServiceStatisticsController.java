package ecommerce.accountmanagement.controller;

import ecommerce.accountmanagement.entity.Account;
import ecommerce.accountmanagement.service.AccountService;
import ecommerce.accountmanagement.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping(value = "/service-statistics")
@AllArgsConstructor
public class ServiceStatisticsController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @GetMapping
    public String showServiceStatistics(final Model model) {
        final List<Account> accounts = accountService.getAllAccounts();
        final List<String> accountHolderNames = accounts.stream()
                .map(Account::getAccountId)
                .map(accountService::getAccountHolderName)
                .toList();

        model.addAttribute("accountHolderNames", accountHolderNames);
        model.addAttribute("allAccounts", accounts);

        return "service-statistics";
    }

    @GetMapping("/all-users-statistics")
    public String getAllUsersStatistics(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
                                        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
                                        final Model model) {

        model.addAttribute("totalDeposits", transactionService.getTotalDeposits(startDate, endDate));
        model.addAttribute("totalWithdrawals", transactionService.getTotalWithdrawals(startDate, endDate));

        return showServiceStatistics(model);
    }
}
