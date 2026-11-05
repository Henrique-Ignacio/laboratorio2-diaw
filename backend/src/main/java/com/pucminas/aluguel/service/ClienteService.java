package com.pucminas.aluguel.service;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class ClienteService {
    private final ClienteRepository repo; private final PedidoRepository pedidos; private final UsuarioRepository usuarios;
    public ClienteService(ClienteRepository repo, PedidoRepository pedidos, UsuarioRepository usuarios) { this.repo = repo; this.pedidos = pedidos; this.usuarios = usuarios; }
    private void validar(Cliente c) {
        if (c.rendimentos != null && c.rendimentos.size() > 3) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo de 3 rendimentos");
    }
    public List<Cliente> listar(Usuario u) {
        return u.perfil == Perfil.CLIENTE ? repo.findByUsuarioId(u.id).stream().toList() : repo.findAll();
    }
    public Cliente buscar(Long id, Usuario u) {
        Cliente c = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (u.perfil == Perfil.CLIENTE && (c.usuario == null || !c.usuario.id.equals(u.id))) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return c;
    }
    /** Create: agentes cadastram clientes sem usuário de acesso; clientes se cadastram pelo /auth/register. */
    public Cliente criar(Cliente dados, Usuario u) {
        if (u.perfil == Perfil.CLIENTE) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas agentes");
        validar(dados); dados.id = null; dados.usuario = null; return repo.save(dados);
    }
    public Cliente atualizar(Long id, Cliente dados, Usuario u) {
        Cliente c = buscar(id, u); validar(dados);
        c.nome = dados.nome; c.cpf = dados.cpf; c.rg = dados.rg; c.endereco = dados.endereco; c.profissao = dados.profissao;
        if (dados.rendimentos != null) { c.rendimentos.clear(); c.rendimentos.addAll(dados.rendimentos); }
        return repo.save(c);
    }
    @Transactional
    public void excluir(Long id, Usuario u) {
        Cliente c = buscar(id, u);
        if (pedidos.existsByClienteId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente possui pedidos de aluguel");
        repo.delete(c);
        if (c.usuario != null) usuarios.delete(c.usuario);
    }
}
