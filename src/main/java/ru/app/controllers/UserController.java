package ru.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.app.components.DocumentRight;
import ru.app.components.UserValidator;
import ru.app.models.Role;
import ru.app.models.User;
import ru.app.services.DocumentService;
import ru.app.services.RoleService;
import ru.app.services.SecurityService;
import ru.app.services.UserService;

import java.util.stream.Collectors;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/registration")
    public String registration(Model model) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }

        model.addAttribute("allRoles", roleService.getRoles());
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getRoles());
            return "registration";
        }
        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }

        if (error != null)
            model.addAttribute("error", "Ваш логин и/или пароль не верны.");

        if (logout != null)
            model.addAttribute("message", "Вы успешно вышли из аккаунта.");

        return "login";
    }

    @GetMapping("/me")
    public String getUser(Model model) {
        var user = userService.getUser();
        model.addAttribute("userRole", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
        model.addAttribute("allRoles", roleService.getRoles());
        model.addAttribute("userForm", user);
        return "user";
    }

    @PostMapping("/me")
    public String setUserRole(@ModelAttribute("userForm") User userForm, Model model) {
        var user = userService.getUser();
        user.setRoles(userForm.getRoles());
        userService.save(user);
        model.addAttribute("userRole", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
        model.addAttribute("allRoles", roleService.getRoles());
        model.addAttribute("userForm", userForm);
        return "user";
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable int id, Model model) {
        var user = userService.findById((long) id);
        if (user.isEmpty()) {
            model.addAttribute("reason", "Не найден пользователь с id " + id);
            return "error";
        } else {
            model.addAttribute("userName", user.get().getUsername());
            if (userService.getUser() == user.get())
                model.addAttribute("me", true);
            else {
                model.addAttribute("me", false);
                var dr = new DocumentRight();
                dr.setUserId(user.get().getId());
                model.addAttribute("formObject", dr);
                model.addAttribute("documents",
                        documentService.getDocumentsOfCurrentUser(userService.getUser()));
                model.addAttribute("roles", roleService.getRolesRights());
            }
            return "other_user";
        }
    }

    @PostMapping("/user/right")
    public String addUserRight(@ModelAttribute("formObject") DocumentRight formObject, Model model) {
        var user = userService.findById(formObject.getUserId());
        if (user.isPresent()) {
            documentService.checkDocumentRights(formObject.getDocumentId(), user.get(), formObject.getRoleId());
            return "redirect:/users/";
        } else {
            model.addAttribute("reason", "Не найден пользователь с id " + formObject.getUserId());
            return "error";
        }
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getUserList());
        return "users";
    }
}
