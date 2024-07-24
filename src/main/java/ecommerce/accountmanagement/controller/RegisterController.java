package ecommerce.accountmanagement.controller;

import ecommerce.accountmanagement.entity.User;
import ecommerce.accountmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/register")
@AllArgsConstructor
public class RegisterController {

    private final UserService userService;

    @GetMapping
    public String showRegistrationForm(final Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") final User user, final BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }
        userService.registerUser(user);
        return "redirect:/login";
    }
}
