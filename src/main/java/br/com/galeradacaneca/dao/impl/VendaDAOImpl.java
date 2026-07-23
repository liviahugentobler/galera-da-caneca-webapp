package br.com.galeradacaneca.dao.impl;

import br.com.galeradacaneca.dao.VendaDAO;
import br.com.galeradacaneca.model.Venda;
import br.com.galeradacaneca.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JPA do VendaDAO.
 *
 * SOLID — SRP / DIP.
 */
public class VendaDAOImpl implements VendaDAO {

    @Override
    public void salvar(Venda v) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar venda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Venda v) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar venda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void excluir(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Venda v = em.find(Venda.class, id);
            if (v != null) em.remove(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir venda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Venda> buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Venda.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Venda> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Venda v ORDER BY v.id DESC", Venda.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Venda> listarPorVendedor(int idVendedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Venda v WHERE v.vendedor.id = :id ORDER BY v.id DESC", Venda.class)
                .setParameter("id", idVendedor)
                .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal totalGeral() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Object r = em.createQuery(
                "SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v")
                .getSingleResult();
            return new BigDecimal(r.toString());
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal totalPorVendedor(int idVendedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Object r = em.createQuery(
                "SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v WHERE v.vendedor.id = :id")
                .setParameter("id", idVendedor)
                .getSingleResult();
            return new BigDecimal(r.toString());
        } finally {
            em.close();
        }
    }

    @Override
    public long contarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(v) FROM Venda v").getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long contarPorVendedor(int idVendedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return (long) em.createQuery(
                "SELECT COUNT(v) FROM Venda v WHERE v.vendedor.id = :id")
                .setParameter("id", idVendedor)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
