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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fabien Barbero
 */
public class SQLQueryBuilder
{


    final StringBuilder query = new StringBuilder();
    final List<Object> params = new ArrayList<>();

    public SQLQueryBuilder()
    {
    }

    public SQLQueryBuilder( String queryPart, Object... params )
    {
        append( queryPart, params );
    }

    public SQLQueryBuilder append( String queryPart, Object... params )
    {
        query.append( queryPart );
        if( params.length > 0 ) {
            this.params.addAll( Arrays.asList( params ) );
        }
        return this;
    }

}
