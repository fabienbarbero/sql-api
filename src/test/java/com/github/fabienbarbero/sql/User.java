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

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Fabien Barbero
 */
class User
        implements BaseEntity<String>
{

    public static User newInstance( String name, String email )
    {
        User user = new User();
        user.uuid = UUID.randomUUID().toString();
        user.name = name;
        user.email = email;
        return user;
    }

    private String uuid;
    private String name;
    private String email;

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getEmail()
    {
        return email;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof User ) {
            return uuid.equals( ( ( User ) obj ).uuid );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode( this.uuid );
        return hash;
    }

    @Override
    public String getPrimaryKey()
    {
        return uuid;
    }

}
