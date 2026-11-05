package com.pucminas.aluguel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;
@Entity @Table(name = "cliente")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public String rg, cpf, nome, endereco, profissao;
    @ElementCollection(fetch = FetchType.EAGER) public List<Rendimento> rendimentos = new ArrayList<>(); // máx. 3
    @JsonIgnore @OneToOne public Usuario usuario;
}
