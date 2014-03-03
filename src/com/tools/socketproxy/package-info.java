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

/**
 * A fairly simple socket server that acts as an intermediary between one or many
 * clients and a server.
 * <img src="../../../images/package-info.png"/>
 *
 * @startuml 
 * actor Client
 * participant SocketProxy
 * actor Server
 * SocketProxy -> SocketProxy : start listening for connections
 * Client -> SocketProxy : open connection
 * SocketProxy -> Server : open connection
 * Client -> SocketProxy : send message
 * SocketProxy -> Server : send message
 * Server -> SocketProxy : receive message
 * SocketProxy -> Client : receive message
 * Client -> SocketProxy : close connection
 * SocketProxy -> Server : close connection
 * SocketProxy -> SocketListener : conversation record
 * @enduml 
 * @author maartenl 
 * @see
 * http://docs.oracle.com/javase/ tutorial/networking/sockets/clientServer.html
 */
package com.tools.socketproxy;
