package ru.javamentor.preproject.controller;

import ru.javamentor.preproject.model.Role;
import ru.javamentor.preproject.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class UserController {

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }

    @GetMapping({"/login/process", "/", "login"})
    public String login(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        if (user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList())
                .contains("ADMIN")) {
            return "redirect:/admin/users";
        }
        return "redirect:/user";
    }
}
