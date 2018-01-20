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

import java.io.File;
import java.sql.Connection;

import com.github.fabienbarbero.sql.helper.SQLForeignKey;
import com.github.fabienbarbero.sql.helper.SQLHelper;
import com.github.fabienbarbero.sql.helper.SQLIndex;
import com.github.fabienbarbero.sql.helper.SQLTable;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author Fabien Barbero
 */
public class SQLHelperTest
{

    private SQLiteDataSource ds;
    private File tmpFile;

    @Before
    public void setUp()
            throws Exception
    {
        tmpFile = File.createTempFile( "sqlite-", ".db" ).getCanonicalFile();

        ds = new SQLiteDataSource();
        ds.setEncoding( "UTF-8" );
        ds.setUrl( "jdbc:sqlite:" + tmpFile );
    }

    @After
    public void tearDown()
            throws Exception
    {
        tmpFile.delete();
    }

    @Test
    public void testHelper()
            throws Exception
    {
        try (Connection conn = ds.getConnection()) {
            SQLRunner exec = new SQLRunner( conn );
            SQLHelper helper = new SQLHelper( conn );

            // Create tables
            if ( !helper.isTableExists( "USERS" ) ) {
                exec.execute( new SQLQueryBuilder( "create table USERS ("
                                           + "UUID char(36) primary key, "
                                           + "NAME varchar(128) not null, "
                                           + "EMAIL varchar(128) not null)" ) );
            }
            exec.execute( new SQLQueryBuilder( "create table ORDERS ("
                                       + "UUID char(36) primary key,"
                                       + "VALUE varchar(128) not null,"
                                       + "USER_UUID char(36) not null,"
                                       + "foreign key (USER_UUID) references USERS(UUID) on delete cascade on update cascade )" ) );
            exec.execute( new SQLQueryBuilder( "create index value_idx on ORDERS (VALUE)" ) );

            assertTrue( helper.isTableExists( "USERS" ) );
            assertTrue( helper.isTableExists( "ORDERS" ) );
            assertFalse( helper.isTableExists( "ANOTHER" ) );

            assertTrue( helper.isTableColumnExists( "USERS", "NAME" ) );
            assertFalse( helper.isTableColumnExists( "USERS", "LOCATION" ) );

            assertEquals( 2, helper.getTables().size() );

            SQLTable orderTable = helper.getTable( "ORDERS" );
            assertNotNull( orderTable );
            assertEquals( 3, orderTable.getColumns().size() );
            assertTrue( orderTable.getColumn( "UUID" ).isPrimary() );
            assertFalse( orderTable.getColumn( "USER_UUID" ).getForeignKeys().isEmpty() );
            assertTrue( orderTable.getColumn( "VALUE" ).getForeignKeys().isEmpty() );
            assertTrue( orderTable.getColumn( "VALUE" ).isIndexed() );

            SQLForeignKey fKey = orderTable.getColumn( "USER_UUID" ).getForeignKeys().get( 0 );
            assertNotNull( fKey );
//            assertEquals( "user_fk", fKey.getName() );
            assertEquals( "ORDERS", fKey.getFKTableName() );
            assertEquals( "USER_UUID", fKey.getFKColumnName() );
            assertEquals( "USERS", fKey.getPKTableName() );
            assertEquals( "UUID", fKey.getPKColumnName() );
            assertEquals( SQLForeignKey.Rule.CASCADE, fKey.getUpdateRule() );
            assertEquals( SQLForeignKey.Rule.CASCADE, fKey.getDeleteRule() );

            SQLIndex idx = orderTable.getColumn( "VALUE" ).getIndexes().get( 0 );
            assertNotNull( idx );
            assertEquals( "value_idx", idx.getName() );
            assertEquals( "VALUE", idx.getColumnName() );
        }
    }

}
