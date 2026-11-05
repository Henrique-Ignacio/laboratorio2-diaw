package com.pucminas.aluguel.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name = "contrato_credito")
public class ContratoCredito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @ManyToOne public Usuario banco;
    public BigDecimal valor;
}
