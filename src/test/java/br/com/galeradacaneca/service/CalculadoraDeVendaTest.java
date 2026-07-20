package br.com.galeradacaneca.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculadoraDeVenda")
class CalculadoraDeVendaTest {

    private CalculadoraDeVenda calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraDeVenda();
    }

    @Test
    @DisplayName("Deve calcular o subtotal corretamente preço x quantidade")
    void deveCalcularSubtotalCorretamente() {
        BigDecimal preco = new BigDecimal("25.00");
        int quantidade = 3;

        BigDecimal subtotal = calculadora.calcularSubtotal(preco, quantidade);

        assertEquals(new BigDecimal("75.00"), subtotal);
    }

    @Test
    @DisplayName("Não deve aplicar desconto para quantidade menor que 5 itens")
    void naoDeveAplicarDescontoParaQuantidadeMenorQueCinco() {
        BigDecimal preco = new BigDecimal("10.00");
        int quantidade = 4;

        BigDecimal valorTotal = calculadora.calcularValorTotalComDesconto(preco, quantidade);

        assertEquals(new BigDecimal("40.00"), valorTotal);
    }

    @Test
    @DisplayName("Deve aplicar 5% de desconto para quantidade entre 5 e 9 itens")
    void deveAplicarCincoPorCentoDeDescontoParaQuantidadeEntreCincoENove() {
        BigDecimal preco = new BigDecimal("10.00");
        int quantidade = 5;

        BigDecimal valorTotal = calculadora.calcularValorTotalComDesconto(preco, quantidade);

        assertEquals(new BigDecimal("47.50"), valorTotal);
    }

    @Test
    @DisplayName("Deve aplicar 10% de desconto para quantidade a partir de 10 itens")
    void deveAplicarDezPorCentoDeDescontoParaQuantidadeAPartirDeDez() {
        BigDecimal preco = new BigDecimal("20.00");
        int quantidade = 10;

        BigDecimal valorTotal = calculadora.calcularValorTotalComDesconto(preco, quantidade);

        assertEquals(new BigDecimal("180.00"), valorTotal);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o preço unitário é nulo")
    void deveLancarExcecaoQuandoPrecoForNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularSubtotal(null, 2));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o preço unitário é negativo ou zero")
    void deveLancarExcecaoQuandoPrecoForNegativoOuZero() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularSubtotal(BigDecimal.ZERO, 2));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularSubtotal(new BigDecimal("-5.00"), 2));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a quantidade é zero ou negativa")
    void deveLancarExcecaoQuandoQuantidadeForZeroOuNegativa() {
        BigDecimal preco = new BigDecimal("10.00");

        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularSubtotal(preco, 0));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularSubtotal(preco, -1));
    }
}