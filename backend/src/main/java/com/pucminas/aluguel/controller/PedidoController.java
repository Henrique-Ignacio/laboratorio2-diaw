package com.pucminas.aluguel.controller;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.service.PedidoService;
import com.pucminas.aluguel.service.PedidoService.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService s; public PedidoController(PedidoService s) { this.s = s; }
    @PostMapping public PedidoAluguel criar(@RequestBody NovoPedido n, @RequestAttribute Usuario usuario) { return s.criar(n, usuario); }
    @GetMapping public List<PedidoAluguel> listar(@RequestAttribute Usuario usuario) { return s.listar(usuario); }
    @PutMapping("/{id}") public PedidoAluguel modificar(@PathVariable Long id, @RequestBody NovoPedido n, @RequestAttribute Usuario usuario) { return s.modificar(id, n, usuario); }
    @DeleteMapping("/{id}") public PedidoAluguel cancelar(@PathVariable Long id, @RequestAttribute Usuario usuario) { return s.cancelar(id, usuario); }
    @PatchMapping("/{id}/avaliar") public PedidoAluguel avaliar(@PathVariable Long id, @RequestBody Avaliacao a, @RequestAttribute Usuario usuario) { return s.avaliar(id, a, usuario); }
    @PatchMapping("/{id}/contratar") public PedidoAluguel contratar(@PathVariable Long id, @RequestBody(required = false) Contratacao c, @RequestAttribute Usuario usuario) { return s.contratar(id, c, usuario); }
}
