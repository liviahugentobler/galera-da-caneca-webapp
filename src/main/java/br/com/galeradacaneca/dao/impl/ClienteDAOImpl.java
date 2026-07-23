package br.com.galeradacaneca.dao.impl;

import br.com.galeradacaneca.dao.ClienteDAO;
import br.com.galeradacaneca.model.Cliente;
import br.com.galeradacaneca.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JPA do ClienteDAO.
 *
 * SOLID — SRP: responsabilidade única de executar as operações de
 *   persistência para a entidade Cliente.
 * SOLID — OCP: novos métodos de consulta podem ser adicionados sem
 *   modificar a interface ClienteDAO.
 *
 * Refatoração aplicada:
 *   - Separação da interface da implementação (antes era uma única classe).
 *   - Retorno de Optional onde pode haver ausência de resultado,
 *     eliminando retornos null (code smell "Null Returns").
 */
public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void salvar(Cliente c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Cliente c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void excluir(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, id);
            if (c != null) em.remove(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Cliente.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Cliente c ORDER BY c.nomeCompleto", Cliente.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Cliente> pesquisarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> q = em.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.nomeCompleto) LIKE :nome ORDER BY c.nomeCompleto",
                Cliente.class);
            q.setParameter("nome", "%" + nome.toLowerCase() + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cliente> result = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class)
                .setParameter("cpf", cpf)
                .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }
}
