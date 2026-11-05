package com.pucminas.aluguel.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name = "pedido_aluguel")
public class PedidoAluguel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @ManyToOne public Cliente cliente;
    @ManyToOne(cascade = CascadeType.ALL) public Automovel automovel;
    @OneToOne(cascade = CascadeType.ALL) public ContratoCredito contratoCredito; // opcional
    public LocalDate dataInicio, dataFim, dataContrato; // dataContrato: execução do contrato
    @Enumerated(EnumType.STRING) public StatusPedido status = StatusPedido.PENDENTE;
    public String parecer;
}
