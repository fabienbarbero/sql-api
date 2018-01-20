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

/**
 * Map a SQL record to a Java object (entity).
 *
 * @param <T> The entity type to map
 * @author Fabien Barbero
 */
@FunctionalInterface
public interface SQLRecordMapper<T>
{

    /**
     * Build the entity from the record
     *
     * @param record The record containing the values that be be used to build the Java entity
     * @return The Java entity
     */
    T buildEntity( SQLRecord record );

}
