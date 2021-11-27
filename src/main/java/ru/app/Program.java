package ru.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.app.models.Role;
import ru.app.models.User;
import ru.app.repositories.RoleRepository;
import ru.app.repositories.UserRepository;
import ru.app.services.UserService;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Program {
    public static void main(String[] args) {
        SpringApplication.run(Program.class, args);
    }

    @Bean
    public CommandLineRunner demoData(RoleRepository roleRepository, UserRepository userRepository,
                                      UserService userService) {
        if (roleRepository.findAll().size() == 0) {
            roleRepository.saveAll(List.of(
                    new Role(1L, "ROLE_ADMIN"),
                    new Role(2L, "ROLE_USER"),
                    new Role(3L, "ROLE_VIEWER"),
                    new Role(4L, "ROLE_EDITOR")));
        }
        if (userRepository.findAll().isEmpty()) {
            var user = new User("admin", "P@ssw0rd");
            user.setRoles(Set.of(new Role(1L, "ROLE_ADMIN")));
            userService.save(user);
        }
        return args -> {
        };
    }
}
