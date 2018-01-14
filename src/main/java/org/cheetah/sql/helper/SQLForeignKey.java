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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Fabien Barbero
 */
public class SQLForeignKey
{

    private final String name;
    private final String pkTableName;
    private final String pkColumnName;
    private final String fkTableName;
    private final String fkColumnName;
    private final Rule updateRule;
    private final Rule deleteRule;

    SQLForeignKey( ResultSet rs,
                   Rule updateRule,
                   Rule deleteRule )
            throws SQLException
    {
        this.name = rs.getString( "FK_NAME" );
        this.pkTableName = rs.getString( "PKTABLE_NAME" );
        this.pkColumnName = rs.getString( "PKCOLUMN_NAME" );
        this.fkTableName = rs.getString( "FKTABLE_NAME" );
        this.fkColumnName = rs.getString( "FKCOLUMN_NAME" );
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
    }

    /**
     * Get the key name
     *
     * @return The name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the table containing the primary key
     *
     * @return The table name
     */
    public String getPKTableName()
    {
        return pkTableName;
    }

    /**
     * Get the column containing the primary key
     *
     * @return The column name
     */
    public String getPKColumnName()
    {
        return pkColumnName;
    }

    /**
     * Get the table containing the foreign key
     *
     * @return The table name
     */
    public String getFKTableName()
    {
        return fkTableName;
    }

    /**
     * Get the column containing the foreign key
     *
     * @return The column name
     */
    public String getFKColumnName()
    {
        return fkColumnName;
    }

    public Rule getUpdateRule()
    {
        return updateRule;
    }

    public Rule getDeleteRule()
    {
        return deleteRule;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof SQLForeignKey ) {
            SQLForeignKey key = ( SQLForeignKey ) obj;
            return name.equals( key.name )
                   && pkTableName.equals( key.pkTableName )
                   && pkColumnName.equals( key.pkColumnName )
                   && fkTableName.equals( key.fkTableName )
                   && fkColumnName.equals( key.fkColumnName );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode( this.name );
        hash = 53 * hash + Objects.hashCode( this.pkTableName );
        hash = 53 * hash + Objects.hashCode( this.pkColumnName );
        hash = 53 * hash + Objects.hashCode( this.fkTableName );
        hash = 53 * hash + Objects.hashCode( this.fkColumnName );
        return hash;
    }

    public enum Rule
    {

        SET_DEFAULT( DatabaseMetaData.importedKeySetDefault ),
        RESTRICT( DatabaseMetaData.importedKeyRestrict ),
        SET_NULL( DatabaseMetaData.importedKeySetNull ),
        CASCADE( DatabaseMetaData.importedKeyCascade ),
        NO_ACTION( DatabaseMetaData.importedKeyNoAction );

        private final int value;

        private Rule( int value )
        {
            this.value = value;
        }

        static Rule from( int value )
        {
            return Arrays.stream( values() )
                    .filter( rule -> rule.value == value )
                    .findFirst().get();
        }

    }

}
