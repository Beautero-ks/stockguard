package dev.bops.stockguard.user.application;

import dev.bops.stockguard.shared.infrastructure.JwtTokenProvider;
import dev.bops.stockguard.user.application.dto.*;
import dev.bops.stockguard.user.domain.User;
import dev.bops.stockguard.user.domain.User.Role;
import dev.bops.stockguard.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserResponse register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        Role role = Role.valueOf(command.role().toUpperCase());

        User user = new User(
                command.email(),
                passwordEncoder.encode(command.password()),
                command.fullName(),
                role
        );

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse(token, toUserResponse(user));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.isEnabled()
        );
    }
}