/*
 * Copyright (C) 2016 fabien.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.github.fabienbarbero.sql;

/**
 * DAO using primary key
 *
 * @author Fabien Barbero
 * @param <T>  The entity type
 * @param <PK> The primary key type
 */
public interface BaseDAO<T extends BaseEntity<PK>, PK>
        extends DAO<T>
{

    /**
     * Find an entity by its primary key
     *
     * @param key The primary key value
     * @return The optional entity found
     * @throws SQLFaultException SQL error
     */
    T find( PK key )
            throws SQLFaultException;

    /**
     * Update an entity
     *
     * @param entity The entity to update
     * @throws SQLFaultException SQL error
     */
    void updateEntity( T entity )
            throws SQLFaultException;

    /**
     * Delete an entity by its primary key
     *
     * @param key The primary key identifying the entity to delete
     * @throws SQLFaultException SQL error
     */
    void deleteEntity( PK key )
            throws SQLFaultException;

    /**
     * Delete an entity
     *
     * @param entity The entity to delete
     * @throws SQLFaultException SQL error
     */
    void deleteEntity( T entity )
            throws SQLFaultException;

}
