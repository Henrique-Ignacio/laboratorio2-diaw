package com.pucminas.aluguel.model;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
@Embeddable
public class Rendimento { public String entidadeEmpregadora; public BigDecimal valor; }
