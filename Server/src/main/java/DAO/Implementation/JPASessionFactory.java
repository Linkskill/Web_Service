package DAO.Implementation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A classe JPA (Java Persistence API) cria gerenciadores de entidades que são utilizados pela interface DAO.
 */
public class JPASessionFactory {
    private static EntityManagerFactory sessionFactory;

    /**
     * Inicializa uma sessionFactory que contém o método construtor do gerenciador de entidade.
     */
    public static void initialize() {
        sessionFactory = Persistence.createEntityManagerFactory("webservicesPersistenceUnit");
    }

    /**
     * Cria (por meio da sessionFactory) e retorna o gerenciador de entidades.
     * @return gerenciador de entidades do tipo EntityManager
     */
    public static EntityManager createEntityManager() {
        return sessionFactory.createEntityManager();
    }
}
