package br.com.galeradacaneca.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vendedor.isGerente()")
class VendedorTest {

    @Test
    @DisplayName("Deve retornar true quando o cargo é Gerente")
    void deveRetornarTrueQuandoCargoForGerente() {
        Vendedor vendedor = new Vendedor();
        vendedor.setCargo(new Cargo("Gerente"));

        assertTrue(vendedor.isGerente());
    }

    @Test
    @DisplayName("Deve retornar true quando o cargo é Supervisor, ignorando caixa")
    void deveRetornarTrueQuandoCargoForSupervisorIgnorandoCaixa() {
        Vendedor vendedor = new Vendedor();
        vendedor.setCargo(new Cargo("supervisor"));

        assertTrue(vendedor.isGerente());
    }

    @Test
    @DisplayName("Deve retornar false quando o cargo é Vendedor")
    void deveRetornarFalseQuandoCargoForVendedor() {
        Vendedor vendedor = new Vendedor();
        vendedor.setCargo(new Cargo("Vendedor"));

        assertFalse(vendedor.isGerente());
    }

    @Test
    @DisplayName("Deve retornar false quando o vendedor não possui cargo definido")
    void deveRetornarFalseQuandoCargoForNulo() {
        Vendedor vendedor = new Vendedor();
        vendedor.setCargo(null);

        assertFalse(vendedor.isGerente());
    }
}