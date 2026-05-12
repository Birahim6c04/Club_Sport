package com.esigelec.clubsport.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique DAO — définit les opérations CRUD communes.
 * @param <T>  Type de l'entité
 * @param <ID> Type de la clé primaire
 */
public interface DAO<T, ID> {
    T          insert(T entity)  throws SQLException;
    T          findById(ID id)   throws SQLException;
    List<T>    findAll()         throws SQLException;
    void       update(T entity)  throws SQLException;
    void       delete(ID id)     throws SQLException;
}