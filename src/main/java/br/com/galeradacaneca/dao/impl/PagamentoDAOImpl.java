package br.com.galeradacaneca.dao.impl;

import br.com.galeradacaneca.dao.PagamentoDAO;
import br.com.galeradacaneca.model.Pagamento;
import br.com.galeradacaneca.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JPA do PagamentoDAO.
 *
 * SOLID — SRP / DIP.
 */
public class PagamentoDAOImpl implements PagamentoDAO {

    @Override
    public void salvar(Pagamento p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar pagamento: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Pagamento p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar pagamento: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void excluir(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Pagamento p = em.find(Pagamento.class, id);
            if (p != null) em.remove(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir pagamento: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Pagamento> buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Pagamento.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Pagamento> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Pagamento p ORDER BY p.id DESC", Pagamento.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Pagamento> buscarPorVenda(int idVenda) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Pagamento> result = em.createQuery(
                "SELECT p FROM Pagamento p WHERE p.venda.id = :id", Pagamento.class)
                .setParameter("id", idVenda)
                .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }
}
