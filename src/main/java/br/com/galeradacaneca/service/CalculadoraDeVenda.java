package br.com.galeradacaneca.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regra de negócio de cálculo de valores de venda.
 *
 * SOLID — SRP: responsabilidade única de calcular valores monetários de uma
 * venda (subtotal e desconto), sem qualquer acesso a banco de dados.
 * Foi extraída como classe própria para que a regra de cálculo possa ser
 * testada isoladamente e reaproveitada pela futura camada
 * web, independente da camada de persistência.
 *
 * Regra de desconto aplicada, com base na quantidade de itens vendidos:
 *   - quantidade >= 10 itens: 10% de desconto sobre o subtotal
 *   - quantidade >= 5  itens: 5%  de desconto sobre o subtotal
 *   - quantidade <  5  itens: sem desconto
 */
public class CalculadoraDeVenda {

    private static final BigDecimal LIMITE_DESCONTO_10 = BigDecimal.valueOf(10);
    private static final BigDecimal LIMITE_DESCONTO_5   = BigDecimal.valueOf(5);
    private static final int QUANTIDADE_MINIMA_10 = 10;
    private static final int QUANTIDADE_MINIMA_5  = 5;

    public BigDecimal calcularSubtotal(BigDecimal precoUnitario, int quantidade) {
        validarPreco(precoUnitario);
        validarQuantidade(quantidade);
        return precoUnitario
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularPercentualDesconto(int quantidade) {
        validarQuantidade(quantidade);
        if (quantidade >= QUANTIDADE_MINIMA_10) {
            return LIMITE_DESCONTO_10.divide(BigDecimal.valueOf(100));
        }
        if (quantidade >= QUANTIDADE_MINIMA_5) {
            return LIMITE_DESCONTO_5.divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularValorTotalComDesconto(BigDecimal precoUnitario, int quantidade) {
        BigDecimal subtotal = calcularSubtotal(precoUnitario, quantidade);
        BigDecimal percentualDesconto = calcularPercentualDesconto(quantidade);
        BigDecimal valorDesconto = subtotal.multiply(percentualDesconto);
        return subtotal.subtract(valorDesconto).setScale(2, RoundingMode.HALF_UP);
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero.");
        }
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
    }
}