package rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import rest.security.JwtUtil;
import rest.dto.LoginDTO;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {
        System.out.println("In login method user " + dto.getUsername());
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    dto.getUsername(), dto.getPassword());
            var authentication = authenticationManager.authenticate(authenticationToken);
            var jwt = jwtUtil.generateToken(authentication.getName());
            return jwt;
        } catch (AuthenticationException e) {
            System.out.println("Eroare " + e);
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}