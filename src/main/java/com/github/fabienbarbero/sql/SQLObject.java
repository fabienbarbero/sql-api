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

import java.sql.Connection;

/**
 * Interface to implements to save custom objects in the database.
 *
 * @author Fabien Barbero
 */
@FunctionalInterface
public interface SQLObject
{

    /**
     * Convert the current object to SQL object (String, Blob ...)
     *
     * @param conn The SQL connection used to create Blob or Clob
     * @return The SQL object
     * @throws SQLFaultException Error
     */
    Object toSQLObject( Connection conn )
            throws SQLFaultException;

}
