package br.com.galeradacaneca.dao.impl;

import br.com.galeradacaneca.dao.VendedorDAO;
import br.com.galeradacaneca.model.Venda;
import br.com.galeradacaneca.model.Vendedor;
import br.com.galeradacaneca.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JPA do VendedorDAO.
 *
 * SOLID — SRP / OCP / DIP.
 *
 * Refatoração aplicada:
 *   - Separação interface / implementação.
 *   - autenticar() retorna Optional em vez de null.
 */
public class VendedorDAOImpl implements VendedorDAO {

    @Override
    public void salvar(Vendedor v) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar funcionário: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Vendedor v) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar funcionário: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void excluir(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Vendedor v = em.find(Vendedor.class, id);
            if (v != null) em.remove(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir funcionário: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Vendedor> buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Vendedor.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vendedor> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Vendedor v ORDER BY v.nomeCompleto", Vendedor.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vendedor> pesquisarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Vendedor> q = em.createQuery(
                "SELECT v FROM Vendedor v WHERE LOWER(v.nomeCompleto) LIKE :nome ORDER BY v.nomeCompleto",
                Vendedor.class);
            q.setParameter("nome", "%" + nome.toLowerCase() + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Vendedor> autenticar(String email, String senha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Vendedor> result = em.createQuery(
                "SELECT v FROM Vendedor v WHERE v.email = :email AND v.senha = :senha",
                Vendedor.class)
                .setParameter("email", email)
                .setParameter("senha", senha)
                .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public void alterarSenha(int idVendedor, String novaSenha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Vendedor v = em.find(Vendedor.class, idVendedor);
            if (v != null) {
                v.setSenha(novaSenha);
                em.merge(v);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao alterar senha: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public double totalVendasPorVendedor(int idVendedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Object result = em.createQuery(
                "SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v WHERE v.vendedor.id = :id")
                .setParameter("id", idVendedor)
                .getSingleResult();
            return ((Number) result).doubleValue();
        } finally {
            em.close();
        }
    }
}
