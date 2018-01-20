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

import java.util.List;

/**
 * @author Fabien Barbero
 */
public abstract class AbstractDAO<E extends Entity>
        extends SQLRunner
        implements DAO<E>, SQLRecordMapper<E>
{
    protected final String tableName;

    public AbstractDAO( String tableName, HasSQLConnection conn )
    {
        super( conn );
        this.tableName = tableName;
    }

    @Override
    public List<E> findAll()
            throws SQLFaultException
    {
        return query( this, new SQLQueryBuilder( "select * from " + tableName ) );
    }

}
