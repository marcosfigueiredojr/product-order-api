package br.com.marcos.product_order_application.controller;

import br.com.marcos.product_order_application.dto.*;
import br.com.marcos.product_order_infrastructure.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // Ajustado para bater com o seu Postman que está usando /auth/login
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
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            // 1. Tenta autenticar
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            // 2. Se passar, gera o token usando as autoridades vindas do objeto 'authentication' (já autenticado)
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String token = jwtService.generateToken(request.getUsername(), role);

            return ResponseEntity.ok(new AuthResponseDTO(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");
        } catch (Exception e) {
            // ISSO VAI FAZER O ERRO APARECER NO CONSOLE DO ECLIPSE:
            System.err.println("ERRO NO LOGIN: " + e.getMessage());
            e.printStackTrace(); 
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }
}