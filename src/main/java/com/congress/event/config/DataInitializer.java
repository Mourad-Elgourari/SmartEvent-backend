package com.congress.event.config;

import com.congress.event.enums.Civility;
import com.congress.event.enums.Role;
import com.congress.event.model.User;
import com.congress.event.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer un utilisateur admin par défaut s'il n'existe pas
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setCivility(Civility.MR);
            admin.setPassword(passwordEncoder.encode("admin123")); // Mot de passe par défaut
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("✅ Utilisateur admin créé : username='admin', password='admin123'");
        } else {
            System.out.println("ℹ️  Utilisateur admin existe déjà.");
        }
    }
}
