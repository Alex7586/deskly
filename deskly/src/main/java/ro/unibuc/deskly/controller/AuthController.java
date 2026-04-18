package ro.unibuc.deskly.controller;

import ro.unibuc.deskly.dto.*;
import ro.unibuc.deskly.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request,
                                              HttpSession session,
                                              HttpServletRequest httpRequest){
        AuthResponse response = authService.login(request, session, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(HttpSession session){
        CurrentUserResponse response = authService.getCurrentUser(session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpSession  session,
                                               HttpServletRequest httpRequest){
        AuthResponse response = authService.logout(session, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody ForgotPasswordRequest request,
                                                       HttpServletRequest httpRequest){
        AuthResponse response = authService.forgotPassword(request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody ResetPasswordRequest request,
                                                      HttpServletRequest httpRequest){
        AuthResponse response = authService.resetPassword(request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

}
