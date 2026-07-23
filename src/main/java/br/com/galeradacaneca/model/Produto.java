package br.com.galeradacaneca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade Produto.
 *
 * SOLID — SRP: modela exclusivamente os dados de um produto.
 * A lógica de estoque foi extraída para ProdutoService.
 */
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer id;

    @Column(name = "nome_prod", nullable = false, unique = true, length = 45)
    private String nomeProd;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "saidas", nullable = false)
    private Integer saidas;

    @Column(name = "entradas", nullable = false)
    private Integer entradas;

    public Produto() {}

    public Integer getId()                     { return id; }
    public void setId(Integer id)              { this.id = id; }
    public String getNomeProd()                { return nomeProd; }
    public void setNomeProd(String nome)       { this.nomeProd = nome; }
    public BigDecimal getPreco()               { return preco; }
    public void setPreco(BigDecimal preco)     { this.preco = preco; }
    public Integer getQuantidade()             { return quantidade; }
    public void setQuantidade(Integer qtd)     { this.quantidade = qtd; }
    public Integer getSaidas()                 { return saidas; }
    public void setSaidas(Integer saidas)      { this.saidas = saidas; }
    public Integer getEntradas()               { return entradas; }
    public void setEntradas(Integer entradas)  { this.entradas = entradas; }

    @Override
    public String toString() { return nomeProd; }
}
