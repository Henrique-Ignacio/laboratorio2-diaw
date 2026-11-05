package com.pucminas.aluguel.repository;
import com.pucminas.aluguel.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ClienteRepository extends JpaRepository<Cliente, Long> { Optional<Cliente> findByUsuarioId(Long usuarioId); }
