package br.com.galeradacaneca.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica para operações CRUD.
 *
 * SOLID — ISP (Interface Segregation Principle):
 *   Define apenas as operações básicas comuns a todos os DAOs.
 *   DAOs especializados podem estender esta interface e acrescentar
 *   métodos específicos sem obrigar outros implementadores.
 *
 * SOLID — DIP (Dependency Inversion Principle):
 *   As camadas de serviço dependem desta abstração, não das
 *   implementações concretas.
 *
 * @param <T>  Tipo da entidade gerenciada.
 * @param <ID> Tipo da chave primária.
 */
public interface GenericDAO<T, ID> {

    void salvar(T entidade);

    void atualizar(T entidade);

    void excluir(ID id);

    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();
}
