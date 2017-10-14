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

import java.io.File;
import org.cheetah.sql.helper.SQLHelper;
import org.junit.After;
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

        try (SQLTransaction tx = SQLTransaction.begin( ds )) {
            ensureUserTableCreated( tx );
            SQLExecutor exec = new SQLExecutor( tx );
            exec.execute( SQLQuery.of( "delete from USERS" ) );
            tx.commit();
        }
    }

    @After
    public void tearDown()
            throws Exception
    {
        tmpFile.delete();
    }

    @Test
    public void testSimpleQueries()
            throws Exception
    {
        try (SQLTransaction tx = SQLTransaction.begin( ds )) {
            SQLExecutor exec = new SQLExecutor( tx );
            UserDAO userDAO = new UserDAOImpl( tx );

            // Create new table
            assertEquals( 0, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );

            // Insert new entity
            User user = User.newInstance( "john doe", "john@doe.com" );
            userDAO.addEntity( user );

            assertEquals( 1, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );
            assertEquals( user, userDAO.find( user.getUuid() ) );
            assertEquals( user, userDAO.findByEmail( "john@doe.com" ) );
            assertEquals( "john doe", userDAO.findByEmail( "john@doe.com" ).getName() );
            assertEquals( "john@doe.com", userDAO.findByEmail( "john@doe.com" ).getEmail() );

            assertNotNull( userDAO.find( "1234567890" ) );
            assertNull( userDAO.findByEmail( "jane@doe.com" ) );

            userDAO.deleteEntity( user );
            assertNull( userDAO.find( user.getUuid() ) );
        }
    }

    @Test
    public void testTransactionCommit()
            throws Exception
    {
        try (SQLTransaction tx1 = SQLTransaction.begin( ds )) {
            SQLExecutor exec = new SQLExecutor( tx1 );
            UserDAO userDAO = new UserDAOImpl( tx1 );

            // Create new table
            ensureUserTableCreated( tx1 );
            tx1.commit();
            assertEquals( 0, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );

            // Insert new entity
            User user = User.newInstance( "john doe", "john@doe.com" );
            userDAO.addEntity( user );

            assertEquals( 1, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );

            try (SQLTransaction tx2 = SQLTransaction.begin( ds )) {
                SQLExecutor exec2 = new SQLExecutor( tx2 );

                // First transaction has not commited
                assertEquals( 0, exec2.count( SQLQuery.of( "select count(*) from USERS" ) ) );
            }
            
            tx1.commit();
        }

        // Another transaction
        try (SQLTransaction tx = SQLTransaction.begin( ds )) {
            SQLExecutor exec = new SQLExecutor( tx );
            assertEquals( 1, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );
        }
    }

    @Test
    public void testTransactionRollback()
            throws Exception
    {
        try (SQLTransaction tx = SQLTransaction.begin( ds )) {
            SQLExecutor exec = new SQLExecutor( tx );
            UserDAO userDAO = new UserDAOImpl( tx );

            // Create new table
            assertEquals( 0, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );

            // Insert new entity
            User user = User.newInstance( "john doe", "john@doe.com" );
            userDAO.addEntity( user );

            assertEquals( 1, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );
            tx.rollback();
        }

        // Another transaction
        try (SQLTransaction tx = SQLTransaction.begin( ds )) {
            SQLExecutor exec = new SQLExecutor( tx );
            assertEquals( 0, exec.count( SQLQuery.of( "select count(*) from USERS" ) ) );
        }
    }

    private void ensureUserTableCreated( SQLTransaction tx )
    {
        SQLExecutor exec = new SQLExecutor( tx );
        SQLHelper helper = new SQLHelper( tx );
        if ( !helper.isTableExists( "USERS" ) ) {
            exec.execute( SQLQuery.of( "create table USERS ("
                                       + "UUID char(36) primary key, "
                                       + "NAME varchar(128) not null, "
                                       + "EMAIL varchar(128) not null)" ) );
        }
    }

}
