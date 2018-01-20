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
package org.kalgan.sql.helper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Fabien Barbero
 */
public class SQLTable
{

    private final String name;
    private final List<SQLColumn> columns;

    SQLTable( String name, List<SQLColumn> columns )
    {
        this.name = name;
        this.columns = columns;
    }

    /**
     * Get the table columns
     *
     * @return The columns
     */
    public List<SQLColumn> getColumns()
    {
        return columns;
    }

    public SQLColumn getColumn( String name )
    {
        return columns.stream()
                .filter( col -> col.getName().equals( name ) )
                .findFirst().orElse( null );
    }

    /**
     * Get the primary columns for this table
     *
     * @return The columns
     */
    public List<SQLColumn> getPrimaryColumns()
    {
        return columns.stream().filter( SQLColumn::isPrimary ).collect( Collectors.toList() );
    }

    /**
     * Get the table name
     *
     * @return The name
     */
    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof SQLTable ) {
            SQLTable table = ( SQLTable ) obj;
            return name.equals( table.name );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode( this.name );
        return hash;
    }

}
