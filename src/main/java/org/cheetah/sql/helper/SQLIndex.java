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

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Fabien Barbero
 */
public class SQLIndex
{

    private final String name;
    private final String tableName;
    private final String columnName;
    private final Ordering ordering;

    SQLIndex( String tableName,
              String name,
              String columnName,
              Ordering ordering )
    {
        this.name = name;
        this.tableName = tableName;
        this.columnName = columnName;
        this.ordering = ordering;
    }

    /**
     * Get the index name
     *
     * @return The name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the table owning the index
     *
     * @return The table name
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * Get the column owning the index
     *
     * @return The column name
     */
    public String getColumnName()
    {
        return columnName;
    }

    /**
     * Get the index ordering
     *
     * @return The ordering
     */
    public Ordering getOrdering()
    {
        return ordering;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof SQLIndex ) {
            SQLIndex index = ( SQLIndex ) obj;
            return name.equals( index.name )
                   && tableName.equals( index.tableName )
                   && columnName.equals( index.columnName );

        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode( this.name );
        hash = 97 * hash + Objects.hashCode( this.tableName );
        hash = 97 * hash + Objects.hashCode( this.columnName );
        return hash;
    }

    public enum Ordering
    {
        ASC( "A" ),
        DESC( "D" );

        private final String value;

        private Ordering( String value )
        {
            this.value = value;
        }

        static Ordering from( String value )
        {
            return Arrays.stream( values() )
                    .filter( o -> o.value.equals( value ) )
                    .findFirst().orElse( null );
        }

    }

}
