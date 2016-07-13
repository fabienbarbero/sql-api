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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author Fabien Barbero
 */
public class SQLIterator<T>
        implements Iterator<T>, AutoCloseable
{

    private final ResultSet rs;
    private final SQLRecordMapper<T> mapper;
    private final PreparedStatement st;

    SQLIterator( ResultSet rs, PreparedStatement st, SQLRecordMapper<T> mapper )
    {
        this.rs = rs;
        this.st = st;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext()
    {
        try {
            return rs.next();
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting next entity", ex );
        }
    }

    @Override
    public T next()
    {
        return mapper.buildEntity( new SQLRecord( rs ) );
    }

    @Override
    public void close()
            throws Exception
    {
        rs.close();
        st.close();
    }

}
