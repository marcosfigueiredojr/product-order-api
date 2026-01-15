package br.com.marcos.product_order_application.controller;

import br.com.marcos.product_order_application.dto.*;
import br.com.marcos.product_order_infrastructure.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        authenticationManager.authenticate(authentication);

        String token = jwtService.generateToken(
                request.getUsername(),
                authentication.getAuthorities().iterator().next().getAuthority()
        );

        return new AuthResponseDTO(token);
    }
}
