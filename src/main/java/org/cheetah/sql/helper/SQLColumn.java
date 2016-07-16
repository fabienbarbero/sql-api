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
package org.cheetah.sql.helper;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Fabien Barbero
 */
public class SQLColumn
{

    private final String name;
    private final int type;
    private final int size;
    private final boolean nullable;
    private final String tableName;
    private final boolean primary;
    private final List<SQLIndex> indexes;
    private final List<SQLForeignKey> foreignKeys;

    SQLColumn( String name,
               int type,
               int size,
               boolean nullable,
               boolean primary,
               String tableName,
               List<SQLIndex> indexes,
               List<SQLForeignKey> foreignKeys )
    {
        this.name = name;
        this.type = type;
        this.size = size;
        this.nullable = nullable;
        this.primary = primary;
        this.tableName = tableName;
        this.indexes = indexes;
        this.foreignKeys = foreignKeys;
    }

    /**
     * Get the column name
     *
     * @return The name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Indicates if this column is a primary key
     *
     * @return true if this is a primary key
     */
    public boolean isPrimary()
    {
        return primary;
    }

    /**
     * Get the column type (see java.sql.Types)
     *
     * @return The type
     */
    public int getType()
    {
        return type;
    }

    /**
     * Get the column size
     *
     * @return The size
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Indicates if a column allows null values
     *
     * @return true if nullable
     */
    public boolean isNullable()
    {
        return nullable;
    }

    /**
     * Get the table owning this column
     *
     * @return The table name
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * Get the indexes for this column
     *
     * @return The indexes
     */
    public List<SQLIndex> getIndexes()
    {
        return indexes;
    }

    /**
     * Indicates if this column is indexed
     *
     * @return trus if at least one index in set for this column
     */
    public boolean isIndexed()
    {
        return !indexes.isEmpty();
    }

    /**
     * Get the foreign keys for this column
     *
     * @return The foreign keys
     */
    public List<SQLForeignKey> getForeignKeys()
    {
        return foreignKeys;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof SQLColumn ) {
            SQLColumn col = ( SQLColumn ) obj;
            return name.equals( col.name ) && tableName.equals( col.tableName );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode( this.name );
        hash = 61 * hash + Objects.hashCode( this.tableName );
        return hash;
    }

}
