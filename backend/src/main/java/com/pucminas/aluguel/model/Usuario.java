package com.pucminas.aluguel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
@Entity @Table(name = "usuario")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(unique = true, nullable = false) public String login;
    @JsonIgnore public String senhaHash;
    @Enumerated(EnumType.STRING) public Perfil perfil;
    @JsonIgnore public String token;
}
