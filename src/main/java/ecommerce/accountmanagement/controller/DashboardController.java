package ecommerce.accountmanagement.controller;

import ecommerce.accountmanagement.enums.UserRole;
import ecommerce.accountmanagement.service.AccountService;
import ecommerce.accountmanagement.util.UserDetailsUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
@AllArgsConstructor
public class DashboardController {

    private final AccountService accountService;

    @GetMapping
    public String showDashboard(final Model model) {
        model.addAttribute("username", UserDetailsUtil.getCurrentUsername());
        model.addAttribute("serviceUser", UserDetailsUtil.hasRole(UserRole.SERVICE));
        model.addAttribute("userHasAccount", accountService.userHasAccount());

        return "dashboard";
    }
}
