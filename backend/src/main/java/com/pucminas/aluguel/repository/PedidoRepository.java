package com.pucminas.aluguel.repository;
import com.pucminas.aluguel.model.PedidoAluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PedidoRepository extends JpaRepository<PedidoAluguel, Long> {
    List<PedidoAluguel> findByClienteId(Long clienteId);
    boolean existsByClienteId(Long clienteId);
}
