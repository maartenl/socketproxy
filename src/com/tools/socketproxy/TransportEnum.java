/*
 * Copyright (C) 2014 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tools.socketproxy;

/**
 * What kind of message is it?
 * @author maartenl
 */
public enum TransportEnum {
    /**
     * From the server to the client.
     */
    SERVER, 
    /**
     * From the client to the server.
     */
    CLIENT, 
    /**
     * Client has closed the connection. Usually no message attached.
     */
    CLIENT_CLOSED_CONNECTION, 
    /**
     * Server has closed the connection. Usually no message attached.
     */
    SERVER_CLOSED_CONNECTION, 
    /**
     * Unable to connect to the server. Though this is a problem,
     * the proxy socket server will still accept incoming connections
     * and record their... one-sided... conversation.
     */
    CANNOT_CONNECT_TO_SERVER
}
