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
package org.cheetah.sql;

/**
 * @author Fabien Barbero
 */
public abstract class AbstractBaseDAO<E extends BaseEntity<PK>, PK>
        extends AbstractDAO<E>
        implements BaseDAO<E, PK>
{
    private final String primaryColumnName;

    public AbstractBaseDAO( String tableName, String primaryColumnName, HasSQLConnection conn )
    {
        super( tableName, conn );
        this.primaryColumnName = primaryColumnName;
    }

    @Override
    public E find( PK key )
            throws SQLFaultException
    {
        return querySingle( this, new SQLQueryBuilder( "select * from " + tableName + " where " + primaryColumnName + " = ?", key ) );
    }

    @Override
    public void deleteEntity( PK key )
            throws SQLFaultException
    {
        execute( new SQLQueryBuilder( "delete from " + tableName + " where " + primaryColumnName + " = ?", key ) );
    }

    @Override
    public void deleteEntity( E entity )
            throws SQLFaultException
    {
        deleteEntity( entity.getPrimaryKey() );
    }

}
