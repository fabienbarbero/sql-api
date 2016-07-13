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

import org.cheetah.sql.SQLExecutor;
import org.cheetah.sql.SQLQuery;
import org.cheetah.sql.SQLTransaction;
import java.io.File;
import java.util.UUID;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author Fabien Barbero
 */
public class SQLiteTest
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

    public void tearDown()
            throws Exception
    {
        tmpFile.delete();
    }

    @Test
    public void testQueries()
            throws Exception
    {
        try (SQLTransaction tx = SQLTransaction.begin( ds )) {
            SQLExecutor exec = new SQLExecutor( tx );
            UserDAO userDAO = new UserDAOImpl( tx );

            // Create new table
            exec.execute( SQLQuery.of( "create table USERS ("
                                       + "UUID char(36) primary key, "
                                       + "NAME varchar(128) not null, "
                                       + "EMAIL varchar(128) not null)" ) );

            assertEquals( 0, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );

            // Insert new entity
            User user = User.newInstance( "john doe", "john@doe.com" );
            userDAO.addEntity( user );

            assertEquals( 1, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );
            assertEquals( user, userDAO.find( user.getUuid() ).get() );
            assertEquals( user, userDAO.findByEmail( "john@doe.com" ) );
            assertEquals( "john doe", userDAO.findByEmail( "john@doe.com" ).getName() );
            assertEquals( "john@doe.com", userDAO.findByEmail( "john@doe.com" ).getEmail() );
            
            assertFalse( userDAO.find( "1234567890" ).isPresent() );
            assertNull( userDAO.findByEmail( "jane@doe.com" ) );
        }
    }

}
