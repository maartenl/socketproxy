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

import java.io.IOException;

/**
 * A probably command line would be: "java -classpath socketproxy.jar Main 3300
 * foreign.server.com 80".
 *
 * @author maartenl
 */
public class Main {

    /**
     * @param args the command line arguments, needs at least three,
     * the proxy port to listen to for client connections, 
     * the hostname of the server and port of the
     * server to redirect traffic to.
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        int proxyPort = Integer.parseInt(args[0]);
        String serverHost = args[1];
        int serverPort = Integer.parseInt(args[2]);
        SocketProxyServer proxy = new SocketProxyServer(proxyPort, serverHost, serverPort);
        SocketListener listener = new SimpleSocketListener();
        proxy.addListener(listener);
        proxy.startServer();
    }
}
