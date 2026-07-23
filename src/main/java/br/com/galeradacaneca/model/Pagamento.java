package br.com.galeradacaneca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade Pagamento.
 *
 * SOLID — SRP: modela exclusivamente os dados de um pagamento.
 */
@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamentos")
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vendas", nullable = false, unique = true)
    private Venda venda;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "forma_pagamento", nullable = false, length = 20)
    private String formaPagamento;

    public Pagamento() {}

    public Integer getId()                      { return id; }
    public void setId(Integer id)               { this.id = id; }
    public Venda getVenda()                     { return venda; }
    public void setVenda(Venda venda)           { this.venda = venda; }
    public BigDecimal getValorTotal()           { return valorTotal; }
    public void setValorTotal(BigDecimal val)   { this.valorTotal = val; }
    public String getFormaPagamento()           { return formaPagamento; }
    public void setFormaPagamento(String forma) { this.formaPagamento = forma; }
}
