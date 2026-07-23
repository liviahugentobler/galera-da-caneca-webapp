package br.com.galeradacaneca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Venda.
 *
 * SOLID — SRP: modela exclusivamente os dados de uma venda.
 */
@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vendas")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vendedores", nullable = true)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto", nullable = true)
    private Produto produto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "data_venda", insertable = false, updatable = false)
    private LocalDateTime dataVenda;


    public Venda() {}

    public Integer getId()                    { return id; }
    public void setId(Integer id)             { this.id = id; }
    public Vendedor getVendedor()             { return vendedor; }
    public void setVendedor(Vendedor v)       { this.vendedor = v; }
    public Produto getProduto()               { return produto; }
    public void setProduto(Produto p)         { this.produto = p; }
    public Cliente getCliente()               { return cliente; }
    public void setCliente(Cliente c)         { this.cliente = c; }
    public BigDecimal getValorTotal()         { return valorTotal; }
    public void setValorTotal(BigDecimal val) { this.valorTotal = val; }
    public Integer getQuantidade()            { return quantidade; }
    public void setQuantidade(Integer qtd)    { this.quantidade = qtd; }
    public java.time.LocalDateTime getDataVenda()          { return dataVenda; }
    public void setDataVenda(java.time.LocalDateTime data) { this.dataVenda = data; }
}
