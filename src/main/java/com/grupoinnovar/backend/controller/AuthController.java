package com.grupoinnovar.backend.controller;

import com.grupoinnovar.backend.dto.LoginRequest;
import com.grupoinnovar.backend.dto.LoginResponse;
import com.grupoinnovar.backend.dto.RegisterRequest;
import com.grupoinnovar.backend.security.JwtTokenProvider;
import com.grupoinnovar.backend.model.Usuario;
import com.grupoinnovar.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          JwtTokenProvider jwtTokenProvider,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Autenticar las credenciales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token JWT
        String token = jwtTokenProvider.generateToken(authentication);

        // Devolver token y datos básicos del usuario
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getNombre()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setActivo(true);
        usuario.setInvitado(false);

        usuarioRepository.save(usuario);

        // Autenticamos automáticamente después de registrarse
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getNombre()));
    }

}
