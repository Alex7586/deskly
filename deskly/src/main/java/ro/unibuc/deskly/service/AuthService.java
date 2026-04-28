package ro.unibuc.deskly.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import ro.unibuc.deskly.dto.*;
import ro.unibuc.deskly.entity.User;
import ro.unibuc.deskly.entity.PasswordResetToken;
import ro.unibuc.deskly.repository.UserRepository;
import ro.unibuc.deskly.repository.PasswordResetTokenRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_SECONDS = 300;

    public AuthService(UserRepository userRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       AuditService auditService,
                       PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.auditService = auditService;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isUserLocked(User user){
        if(user.getLockUntil() == null)
            return false;
        if(Instant.now().isAfter(user.getLockUntil())){
            user.setLocked(false);
            user.setLockUntil(null);
            user.setFailedAttempts(0);
            userRepository.save(user);
            return false;
        }
        return Boolean.TRUE.equals(user.getLocked());
    }

    private void handleFailedLogin(User user, String ipAddress){
        int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
        attempts++;
        user.setFailedAttempts(attempts);
        if(attempts >= MAX_FAILED_ATTEMPTS){
            user.setLocked(true);
            user.setLockUntil(Instant.now().plusSeconds(LOCK_DURATION_SECONDS));
        }

        userRepository.save(user);
        auditService.log(user.getId(), "FAILED_LOGIN", "AUTH", null, ipAddress);
    }

    private void resetFailedLoginState(User user){
        user.setFailedAttempts(0);
        user.setLocked(false);
        user.setLockUntil(null);
        userRepository.save(user);
    }

    private void validatePassword(String password){
        if(password == null || password.isBlank())
            throw new RuntimeException("Password is required");
        if(password.length() < 8)
            throw new RuntimeException("Password must have at least 8 characters");

        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if(!hasLetter || !hasDigit)
            throw new RuntimeException("Password must contain at least one letter and one digit");
    }

    private String generateSecureToken(){
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public AuthResponse register(RegisterRequest request){
        if(request.getEmail() == null || request.getEmail().isBlank())
            throw new RuntimeException("Email is required");

        validatePassword(request.getPassword());

        if(userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("User already exists.");

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                "USER",
                false,
                0,
                Instant.now());

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request, HttpSession session, String ipAddress){
        if(request.getEmail() == null || request.getEmail().isBlank())
            throw new RuntimeException("Email is required");

        if(request.getPassword() == null || request.getPassword().isBlank())
            throw new RuntimeException("Password is required");

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if(userOptional.isEmpty())
            throw new RuntimeException("Invalid credentials");

        User user = userOptional.get();

        if(isUserLocked(user)){
            throw new RuntimeException("Account locked temporarily. Try again later!");
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user, ipAddress);
            throw new RuntimeException("Invalid credentials");
        }

        resetFailedLoginState(user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
        auditService.log(user.getId(),"LOGIN", "AUTH", null, ipAddress);

        return new AuthResponse(
                "Login sucesful",
                user.getId(),
                user.getEmail());
    }

    public CurrentUserResponse getCurrentUser(HttpSession session){
        Object userIdObj = session.getAttribute("userId");

        if(userIdObj == null)
            throw new RuntimeException("User is not authenticated");

        Long userId = (Long) userIdObj;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    public AuthResponse logout(HttpSession session, String ipAddress){
        Object userIdObj = session.getAttribute("userId");
        Long userId = userIdObj instanceof Long ? (Long) userIdObj : null;

        auditService.log(userId, "LOGOUT","AUTH", null, ipAddress);

        session.invalidate();
        return new AuthResponse("Logout successfull",null,null);
    }

    public AuthResponse forgotPassword(ForgotPasswordRequest request, String ipAddress){
        if(request.getEmail() == null || request.getEmail().isBlank())
            throw new RuntimeException("Email is requiered");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String tokenValue = generateSecureToken();

        PasswordResetToken token = new PasswordResetToken(
                tokenValue,
                user,
                Instant.now().plusSeconds(900),
                false);

        passwordResetTokenRepository.save(token);
        auditService.log(user.getId(), "FORGOT_PASSWORD", "PASSWORD_RESET", null, ipAddress);

        return new AuthResponse(
                "If the account exists, a reset process has been initiated",
                null,
                null
        );
    }

    public AuthResponse resetPassword(ResetPasswordRequest request, String ipAddress){
        if(request.getToken() == null || request.getToken().isBlank())
            throw new RuntimeException("Token is required");

        validatePassword(request.getNewPassword());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if(Boolean.TRUE.equals(resetToken.getUsed()))
            throw new RuntimeException("Invalid reset token");

        if(resetToken.getExpiresAt() == null || Instant.now().isAfter(resetToken.getExpiresAt()))
            throw new RuntimeException("Invalid reset token");

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        auditService.log(user.getId(), "RESET_PASSWORD", "PASSWORD_RESET", null, ipAddress);

        return new AuthResponse(
                "Password reset successful",
                user.getId(),
                user.getEmail()
        );
    }
}
