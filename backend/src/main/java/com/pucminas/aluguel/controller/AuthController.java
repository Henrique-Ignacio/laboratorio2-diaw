package com.pucminas.aluguel.controller;
import com.pucminas.aluguel.service.AuthService;
import com.pucminas.aluguel.service.AuthService.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService s; public AuthController(AuthService s) { this.s = s; }
    @PostMapping("/register") public Sessao registrar(@RequestBody Registro r) { return s.registrar(r); }
    @PostMapping("/login") public Sessao login(@RequestBody Login l) { return s.entrar(l); }
}
