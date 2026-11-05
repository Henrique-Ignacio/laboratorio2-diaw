package com.pucminas.aluguel.model;
import jakarta.persistence.*;
@Entity @Table(name = "automovel")
public class Automovel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public String matricula, marca, modelo, placa;
    public Integer ano;
    @Enumerated(EnumType.STRING) public Proprietario proprietario = Proprietario.CLIENTE;
}
