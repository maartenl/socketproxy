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
package socketproxy;

import java.io.IOException;
import java.net.ServerSocket;
import javax.annotation.Nonnull;

/**
 *
 * @author maartenl
 * @see
 * http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */
public class SocketProxyServer {

    private SocketListener listener;
    private final int proxyPort;
    private final int serverPort;
    private final String serverHost;

    /**
     *
     * @param serverHost hostname/ip address of the server to forward messages to.
     * @param proxyPort the port that this deamon will listen on
     * @param serverPort the port that this daemon will forward all messages to
     * the <i>actual</i> server.
     * @throws IOException
     */
    SocketProxyServer(@Nonnull int proxyPort, @Nonnull String serverHost, @Nonnull int serverPort) {
        this.proxyPort = proxyPort;
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    public void startServer() throws IOException {
        System.out.println("Start listening at port " + proxyPort);
        try (
                ServerSocket serverSocket = new ServerSocket(proxyPort)) {
            boolean listening = true;
            while (listening) {
                new SocketProxyThread(serverSocket.accept(), listener, serverHost, serverPort).start();
            }
        }
    }

    public void addListener(SocketListener listener) {
        this.listener = listener;
    }

}
