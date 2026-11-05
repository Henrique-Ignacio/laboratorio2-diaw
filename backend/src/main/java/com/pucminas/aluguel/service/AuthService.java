package com.pucminas.aluguel.service;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
@Service
public class AuthService {
    public record Registro(String login, String senha, Perfil perfil, String nome, String cpf, String rg, String endereco, String profissao, java.util.List<Rendimento> rendimentos) {}
    public record Login(String login, String senha) {}
    public record Sessao(String token, Perfil perfil, String login) {}
    private final UsuarioRepository usuarios; private final ClienteRepository clientes;
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    public AuthService(UsuarioRepository u, ClienteRepository c) { usuarios = u; clientes = c; }
    public Sessao registrar(Registro r) {
        if (r.login() == null || r.senha() == null || r.perfil() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados obrigatórios");
        if (usuarios.findByLogin(r.login()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já existe");
        Usuario u = new Usuario(); u.login = r.login(); u.senhaHash = enc.encode(r.senha()); u.perfil = r.perfil();
        u.token = UUID.randomUUID().toString(); usuarios.save(u);
        if (r.perfil() == Perfil.CLIENTE) {
            Cliente c = new Cliente(); c.usuario = u; c.nome = r.nome(); c.cpf = r.cpf(); c.rg = r.rg(); c.endereco = r.endereco(); c.profissao = r.profissao();
            if (r.rendimentos() != null) {
                if (r.rendimentos().size() > 3) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo de 3 rendimentos");
                c.rendimentos.addAll(r.rendimentos());
            }
            clientes.save(c);
        }
        return new Sessao(u.token, u.perfil, u.login);
    }
    public Sessao entrar(Login l) {
        Usuario u = usuarios.findByLogin(l.login()).filter(x -> enc.matches(l.senha(), x.senhaHash))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos"));
        u.token = UUID.randomUUID().toString(); usuarios.save(u);
        return new Sessao(u.token, u.perfil, u.login);
    }
}
