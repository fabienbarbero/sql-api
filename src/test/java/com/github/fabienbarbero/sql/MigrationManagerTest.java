/*
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

import com.github.fabienbarbero.sql.helper.SQLHelper;
import com.github.fabienbarbero.sql.migration.MigrationContext;
import com.github.fabienbarbero.sql.migration.MigrationManager;
import com.github.fabienbarbero.sql.migration.Migrator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Fabien Barbero
 */
public class MigrationManagerTest
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
    public void testMigration()
            throws Exception
    {
        MigrationManager manager = new MigrationManager( ds );
        manager.register(new CreateTableMigrator());

        // Live-before does nothing
        manager.execute( MigrationManager.Mode.LIVE_BEFORE );
        try( Connection conn = ds.getConnection() ) {
            SQLHelper helper = new SQLHelper( conn );
            assertFalse(helper.isTableExists( "USERS" ));
        }

        // Normal migration
        manager.execute( MigrationManager.Mode.NORMAL );
        try( Connection conn = ds.getConnection() ) {
            SQLHelper helper = new SQLHelper( conn );
            assertTrue(helper.isTableExists( "USERS" ));
        }

        // Live-after does nothing
        manager.execute( MigrationManager.Mode.LIVE_AFTER );
        try( Connection conn = ds.getConnection() ) {
            SQLHelper helper = new SQLHelper( conn );
            assertTrue(helper.isTableExists( "USERS" ));
        }
    }


    private static class CreateTableMigrator extends Migrator
    {

        CreateTableMigrator()
        {
            super( "create-table" );
        }

        @Override
        protected void migrateNormal( MigrationContext context )
                throws Exception
        {
            SQLRunner runner = context.getRunner();

            runner.execute( new SQLQueryBuilder(
                    "create table USERS (UUID varchar(36) primary key, NAME varchar(128) not null)") );
        }
    }

}
