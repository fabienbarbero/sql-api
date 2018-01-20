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
package org.kalgan.sql;

import java.sql.SQLException;

/**
 * @author Fabien Barbero
 */
public class SQLFaultException
        extends RuntimeException
{

    public SQLFaultException( String msg, SQLException ex )
    {
        super( msg, ex );
    }

    /**
     * Get the SQL error code.
     *
     * @return The error code
     */
    public int getErrorCode()
    {
        return getCause().getErrorCode();
    }

    @Override
    public synchronized SQLException getCause()
    {
        return ( SQLException ) super.getCause();
    }

}
