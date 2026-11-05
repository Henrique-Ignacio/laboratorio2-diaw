package com.pucminas.aluguel.controller;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.service.ClienteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService s; public ClienteController(ClienteService s) { this.s = s; }
    @GetMapping public List<Cliente> listar(@RequestAttribute Usuario usuario) { return s.listar(usuario); }
    @PostMapping public Cliente criar(@RequestBody Cliente c, @RequestAttribute Usuario usuario) { return s.criar(c, usuario); }
    @GetMapping("/{id}") public Cliente buscar(@PathVariable Long id, @RequestAttribute Usuario usuario) { return s.buscar(id, usuario); }
    @PutMapping("/{id}") public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente c, @RequestAttribute Usuario usuario) { return s.atualizar(id, c, usuario); }
    @DeleteMapping("/{id}") public void excluir(@PathVariable Long id, @RequestAttribute Usuario usuario) { s.excluir(id, usuario); }
}
