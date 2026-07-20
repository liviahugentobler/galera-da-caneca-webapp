package br.com.galeradacaneca.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Utilitário JPA — mantém uma única instância de EntityManagerFactory (Singleton).
 *
 * SOLID — SRP: responsabilidade exclusiva de gerenciar o ciclo de vida do JPA.
 *
 * Padrão de Projeto — Singleton:
 *   Garante que apenas uma EntityManagerFactory seja criada durante
 *   toda a execução, evitando o custo de múltiplas conexões e
 *   inconsistências de estado.
 *
 * Refatoração aplicada: movido de dao para o pacote util, deixando
 * claro que é uma classe de infraestrutura, não um DAO.
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT = "GaleraDaCanecaPU";
    private static EntityManagerFactory factory;

    /** Construtor privado — impede instanciação (Singleton). */
    private JPAUtil() {}

    public static synchronized EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return factory;
    }

    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
