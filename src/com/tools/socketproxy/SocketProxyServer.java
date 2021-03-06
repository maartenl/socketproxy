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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 * A fairly simple socket server.
 * <img src="../../../images/SocketProxyServer.png"/>
 *
 * @startuml 
 * interface SocketListener
 * interface Runnable
 * interface ExecutorService
 * Runnable: +run()
 * SocketProxyServer: +startServer() 
 * SocketProxyServer: +addListener(listener: SocketListener) 
 * SocketListener <-- SocketProxyServer
 * TalkingProxyThread <-- SocketProxyServer 
 * TalkingServerThread <-- SocketProxyServer 
 * Runnable <|-- TalkingProxyThread
 * Runnable <|-- TalkingServerThread
 * ExecutorService <-- SocketProxyServer 
 * ExecutorService <-- Executors
 * ExecutorService : +execute(r: Runnable)
 * Executors <-- SocketProxyServer 
 * Executors: + {static} newFixedThreadPool(nThreads: int)
 * @enduml 
 * @author maartenl 
 * @see
 * http://docs.oracle.com/javase/ tutorial/networking/sockets/clientServer.html
 */
public class SocketProxyServer
{

    private static final Logger logger = Logger.getLogger(SocketProxyServer.class.getName());

    /**
     * default timeout in milliseconds. 30 seconds ought to do it.
     */
    private static final int SOCKET_TIMEOUT = 5000;

    private SocketListener listener;

    private final int proxyPort;

    private final int serverPort;

    private final String serverHost;

    /**
     * Construction of the socket proxy server.
     * @param serverHost hostname/ip address of the server to forward messages
     * to.
     * @param proxyPort the port that this deamon will listen on.
     * @param serverPort the port that this daemon will forward all messages to
     * on the <i>actual</i> server.
     */
    public SocketProxyServer(@Nonnull int proxyPort, @Nonnull String serverHost, @Nonnull int serverPort)
    {
        this.proxyPort = proxyPort;
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    /**
     * Start the server... will not return.
     * @throws IOException if something goes wrong.
     */
    public void startServer() throws IOException
    {
        logger.log(Level.FINE, "Start listening on port {0}", proxyPort);
        ExecutorService pool = Executors.newFixedThreadPool(50);
        try (
                ServerSocket proxyServerSocket = new ServerSocket(proxyPort))
        {
            boolean listening = true;
            while (listening)
            {
                final Socket clientSocket = proxyServerSocket.accept();
                clientSocket.setSoTimeout(SOCKET_TIMEOUT);
                clientSocket.setKeepAlive(false);
                clientSocket.setSoLinger(false, 0);
                logger.fine("Connection accepted");
                Conversation conversation = new Conversation(
                        clientSocket.getInetAddress().getCanonicalHostName(),
                        clientSocket.getInetAddress().getHostAddress());
                Socket serverSocket = null;
                try
                {
                    serverSocket = new Socket(serverHost, serverPort);
                    serverSocket.setSoTimeout(SOCKET_TIMEOUT);
                    serverSocket.setKeepAlive(false);
                    serverSocket.setSoLinger(false, 0);
                } catch (IOException ex)
                {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                    conversation.addMessage(new Message(TransportEnum.CANNOT_CONNECT_TO_SERVER));
                }
                pool.execute(new TalkingProxyThread(clientSocket, serverSocket, listener, conversation));
                if (serverSocket != null)
                {
                    pool.execute(new TalkingServerThread(clientSocket, serverSocket, conversation));
                }
            }
        }
    }

    /**
     * Adds a listener to the proxy socket server. Bear in mind that currently there
     * is only one listener allowed. Calling this method a second time will
     * replace the existing listener. 
     * @todo yes, I know, the naming is wrong...
     * @param listener the listener to report conversations to.
     */
    public void addListener(SocketListener listener)
    {
        this.listener = listener;
    }

}
