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
package com.github.fabienbarbero.sql.migration;

import com.github.fabienbarbero.sql.SQLRunner;
import com.github.fabienbarbero.sql.SQLTransaction;
import com.github.fabienbarbero.sql.helper.SQLHelper;

/**
 * @author Fabien Barbero
 */
public class MigrationContext
{

    private final SQLTransaction tx;
    private final SQLHelper helper;
    private final SQLRunner runner;

    MigrationContext( SQLTransaction tx ) {
        this.tx = tx;
        this.helper = new SQLHelper( tx );
        this.runner = new SQLRunner( tx );
    }

    public SQLTransaction getTransaction()
    {
        return tx;
    }

    public SQLHelper getHelper()
    {
        return helper;
    }

    public SQLRunner getRunner()
    {
        return runner;
    }
}
