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
package com.github.fabienbarbero.sql.migration;

import com.github.fabienbarbero.sql.SQLTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Use to migrate easily a database. You can modify the structure or insert new values in some table (as you wish!).
 *
 * @author Fabien Barbero
 */
public class MigrationManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( MigrationManager.class );

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private final List<Migrator> migrators = new ArrayList<>();
    private final DataSource dataSource;

    public MigrationManager( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    /**
     * Register a new migrator in the manager
     *
     * @param migrator The migrator to register
     */
    public void register( Migrator migrator )
    {
        migrators.add( migrator );
    }

    /**
     * Execute the migrations
     *
     * @param migrationMode The migration mode to use
     * @throws Exception If the migration fails
     */
    public void execute( Mode migrationMode )
            throws Exception
    {
        Instant start = Instant.now();

        for ( Migrator migrator : migrators ) {
            try ( SQLTransaction tx = SQLTransaction.begin( dataSource ) ) {
                Instant migratorStart = Instant.now();
                MigrationContext context = new MigrationContext( tx );

                switch ( migrationMode ) {
                    case LIVE_BEFORE:
                        migrator.migrateLiveBefore( context );
                        break;
                    case NORMAL:
                        migrator.migrateNormal( context );
                        break;
                    case LIVE_AFTER:
                        migrator.migrateLiveAfter( context );
                        break;
                }
                tx.commit();

                Instant migratorEnd = Instant.now();
                LOGGER.info( ANSI_GREEN + "Migrator '{}' execution succeeded in {} seconds" + ANSI_RESET,
                             migrator.getName(), migratorStart.until( migratorEnd, ChronoUnit.SECONDS ) );

            } catch ( MigrationSkippedException ex ) {
                LOGGER.info( ANSI_YELLOW + "Migrator '{}' skipped" + ANSI_RESET, migrator.getName() );

            } catch ( Exception ex ) {
                LOGGER.error( ANSI_RED + "Migrator '{}' failed" + ANSI_RESET, ex );
                throw ex;
            }
        }

        Instant end = Instant.now();
        LOGGER.info( ANSI_GREEN + "Yippie !!! Migration succeeded in {} seconds" + ANSI_RESET,
                     start.until( end, ChronoUnit.SECONDS ) );
    }

    public enum Mode
    {
        /**
         * The migration will be executed when the server is running, but before the "normal" migration.
         */
        LIVE_BEFORE,
        /**
         * The migration will be executed when the server is stopped.
         */
        NORMAL,
        /**
         * The migration will be executed when the server is running, but after the "normal" migration.
         */
        LIVE_AFTER
    }

}
