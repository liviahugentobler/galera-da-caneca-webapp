package br.com.galeradacaneca.dao.impl;

import br.com.galeradacaneca.dao.CargoDAO;
import br.com.galeradacaneca.model.Cargo;
import br.com.galeradacaneca.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JPA do CargoDAO.
 * SOLID — SRP / DIP.
 */
public class CargoDAOImpl implements CargoDAO {

    @Override
    public void salvar(Cargo c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar cargo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Cargo c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar cargo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void excluir(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cargo c = em.find(Cargo.class, id);
            if (c != null) em.remove(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir cargo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Cargo> buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Cargo.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Cargo> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cargo c ORDER BY c.descricao", Cargo.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Cargo> buscarPorDescricao(String descricao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cargo> result = em.createQuery(
                "SELECT c FROM Cargo c WHERE LOWER(c.descricao) = :desc", Cargo.class)
                .setParameter("desc", descricao.toLowerCase())
                .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }
}
