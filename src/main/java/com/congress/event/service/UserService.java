package com.congress.event.service;

import com.congress.event.enums.Role;
import com.congress.event.model.PasswordResetToken;
import com.congress.event.model.User;
import com.congress.event.model.VerificationToken;
import com.congress.event.repository.PasswordResetTokenRepository;
import com.congress.event.repository.UserRepository;
import com.congress.event.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    private EmailService emailService;

    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    public void deleteVerificationToken(VerificationToken token) {
        tokenRepository.delete(token);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public User registerUser(String username, String email, String password) {
        // Vérifie si le username existe déjà
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Le nom d'utilisateur est déjà utilisé !");
        }

        // Vérifie si l'email existe déjà
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("L'email est déjà utilisé !");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setEnabled(false); // pas encore activé avant verification
        user.setRole(Role.USER);

        userRepository.save(user);

        // Génération du token de verification
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);

        return user;
    }



    public String verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token invalide ou expiré";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return "Votre compte est activé !";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 30 min
        resetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return "Un lien de réinitialisation a été envoyé à votre email.";
    }

    public String resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token invalide ou expiré";
        }

        User user = resetToken.getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
        resetTokenRepository.delete(resetToken);

        return "Mot de passe réinitialisé avec succès.";
    }
}

