package ro.unibuc.deskly.service;

import ro.unibuc.deskly.dto.*;
import ro.unibuc.deskly.entity.User;
import ro.unibuc.deskly.entity.PasswordResetToken;
import ro.unibuc.deskly.repository.UserRepository;
import ro.unibuc.deskly.repository.PasswordResetTokenRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       AuditService auditService){
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.auditService = auditService;
    }

    public AuthResponse register(RegisterRequest request){
        if(request.getEmail() == null || request.getEmail().isBlank())
            throw new RuntimeException("Email is required");

        if(request.getPassword() == null || request.getPassword().isBlank())
            throw new RuntimeException("Password is required");

        if(userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("User already exists.");

        User user = new User(
                request.getEmail(),
                request.getPassword(),
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
            throw new RuntimeException("User not found");

        User user = userOptional.get();

        if(!user.getPasswordHash().equals(request.getPassword()))
            throw new RuntimeException("Wrong password");

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

        String tokenValue = "reset-" + user.getId();

        PasswordResetToken token = new PasswordResetToken(
                tokenValue,
                user,
                Instant.now().plusSeconds(86400),
                false);

        passwordResetTokenRepository.save(token);
        auditService.log(user.getId(), "FORGOT_PASSWORD", "PASSWORD_RESET", null, ipAddress);

        return new AuthResponse(
                "Password reset token generated",
                user.getId(),
                user.getEmail(),
                tokenValue
        );
    }

    public AuthResponse resetPassword(ResetPasswordRequest request, String ipAddress){
        if(request.getToken() == null || request.getToken().isBlank())
            throw new RuntimeException("Token is required");

        if(request.getNewPassword() == null || request.getNewPassword().isBlank())
            throw new RuntimeException("New password is required");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        User user = resetToken.getUser();
        user.setPasswordHash(request.getNewPassword());
        userRepository.save(user);
        auditService.log(user.getId(), "RESET_PASSWORD", "PASSWORD_RESET", null, ipAddress);

        return new AuthResponse(
                "Password reset successful",
                user.getId(),
                user.getEmail()
        );
    }
}
