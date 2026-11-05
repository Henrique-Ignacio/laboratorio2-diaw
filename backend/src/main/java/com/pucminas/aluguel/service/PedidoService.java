package com.pucminas.aluguel.service;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public class PedidoService {
    public record NovoPedido(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {}
    public record Avaliacao(boolean aprovado, String parecer) {}
    public record Contratacao(BigDecimal valorCredito) {}
    private final PedidoRepository pedidos; private final ClienteRepository clientes;
    public PedidoService(PedidoRepository p, ClienteRepository c) { pedidos = p; clientes = c; }
    private Cliente clienteDe(Usuario u) {
        return clientes.findByUsuarioId(u.id).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas clientes"));
    }
    private PedidoAluguel buscar(Long id, Usuario u) {
        PedidoAluguel p = pedidos.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (u.perfil == Perfil.CLIENTE && (p.cliente.usuario == null || !p.cliente.usuario.id.equals(u.id))) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return p;
    }
    private PedidoAluguel pendente(Long id, Usuario u) {
        PedidoAluguel p = buscar(id, u);
        if (p.status != StatusPedido.PENDENTE) throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido não está pendente");
        return p;
    }
    private void apenasAgente(Usuario u) { if (u.perfil == Perfil.CLIENTE) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas agentes"); }
    public PedidoAluguel criar(NovoPedido n, Usuario u) {
        PedidoAluguel p = new PedidoAluguel(); p.cliente = clienteDe(u); p.automovel = n.automovel();
        p.dataInicio = n.dataInicio(); p.dataFim = n.dataFim(); return pedidos.save(p);
    }
    public List<PedidoAluguel> listar(Usuario u) {
        return u.perfil == Perfil.CLIENTE ? pedidos.findByClienteId(clienteDe(u).id) : pedidos.findAll();
    }
    public PedidoAluguel modificar(Long id, NovoPedido n, Usuario u) {
        PedidoAluguel p = pendente(id, u);
        p.automovel.marca = n.automovel().marca; p.automovel.modelo = n.automovel().modelo; p.automovel.ano = n.automovel().ano;
        p.automovel.placa = n.automovel().placa; p.automovel.matricula = n.automovel().matricula; p.automovel.proprietario = n.automovel().proprietario;
        p.dataInicio = n.dataInicio(); p.dataFim = n.dataFim(); return pedidos.save(p);
    }
    public PedidoAluguel cancelar(Long id, Usuario u) { PedidoAluguel p = pendente(id, u); p.status = StatusPedido.CANCELADO; return pedidos.save(p); }
    /** Análise financeira pelo agente: parecer positivo (APROVADO) ou negativo (REJEITADO). */
    public PedidoAluguel avaliar(Long id, Avaliacao a, Usuario u) {
        apenasAgente(u); PedidoAluguel p = pendente(id, u);
        p.status = a.aprovado() ? StatusPedido.APROVADO : StatusPedido.REJEITADO; p.parecer = a.parecer();
        return pedidos.save(p);
    }
    /** Execução do contrato após parecer positivo; banco pode associar contrato de crédito. */
    public PedidoAluguel contratar(Long id, Contratacao c, Usuario u) {
        apenasAgente(u); PedidoAluguel p = buscar(id, u);
        if (p.status != StatusPedido.APROVADO) throw new ResponseStatusException(HttpStatus.CONFLICT, "Apenas pedidos aprovados podem virar contrato");
        if (c != null && c.valorCredito() != null) {
            if (u.perfil != Perfil.BANCO) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas bancos concedem crédito");
            ContratoCredito cc = new ContratoCredito(); cc.banco = u; cc.valor = c.valorCredito(); p.contratoCredito = cc;
        }
        p.status = StatusPedido.CONTRATADO; p.dataContrato = LocalDate.now();
        return pedidos.save(p);
    }
}
