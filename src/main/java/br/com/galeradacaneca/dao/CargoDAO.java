package br.com.galeradacaneca.dao;

import br.com.galeradacaneca.model.Cargo;
import java.util.Optional;

/**
 * Contrato para acesso a dados de Cargo.
 * SOLID — ISP / DIP.
 */
public interface CargoDAO extends GenericDAO<Cargo, Integer> {

    Optional<Cargo> buscarPorDescricao(String descricao);
}
