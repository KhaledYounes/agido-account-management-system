package ecommerce.accountmanagement.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/", method = RequestMethod.GET)
@AllArgsConstructor
public class LoginController {

    @GetMapping(value = {"/", "login"})
    public String showLoginForm() {
        return "login";
    }
}
