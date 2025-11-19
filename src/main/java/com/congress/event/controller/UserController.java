package com.congress.event.controller;

import com.congress.event.enums.Civility;
import com.congress.event.enums.Role;
import com.congress.event.enums.UserStatus;
import com.congress.event.model.User;
import com.congress.event.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Page<User> listUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return userRepository.findAll(pageable);
    }

    // ✅ Add a new user
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default values (optional, in case frontend doesn’t send them)
        if (user.getCivility() == null) {
            user.setCivility(Civility.MR);
        }

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }

        return userRepository.save(user);
    }

}