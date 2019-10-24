package DAO;

import java.util.List;

/**
 * Interface DAO (Data Access Object) que tem por objetivo representar as operações que acessam o banco de dados.
 * @param <T> tipo da entidade que pode assumir os tipos: TicketEntity, AccommodationEntity e PackageEntity.
 */
public interface DAO<T> {
    /**
     * Cria novas entidades e retorna uma confirmação se criado com sucesso.
     * @param entity nova entidade (de um tipo T)
     * @return true se foi criado corretamente, false caso contrário
     */
    public boolean create(T entity);
    
    /**
     * Busca e retorna uma entidade presente no banco de dados usando o id (identificador).
     * @param id do tipo int
     * @return entidade do tipo T
     */
    public T find (int id);

    /**
     * Busca e remove do banco de dados uma entidade identificada por "id".
     * @param id do tipo int
     * @return true se foi removido corretamente, false caso contrário
     */
    public boolean remove (int id);
    
    /**
     * Retorna todas os elementos de uma entidade do tipo T.
     * @return Lista de elementos
     */
    public List<T> getAll();
}