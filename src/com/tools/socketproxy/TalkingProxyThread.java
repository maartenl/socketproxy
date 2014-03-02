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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client is talking to us, and we're sending it onto the server.
 * @see TalkingServerThread
 * @author maartenl
 */
class TalkingProxyThread implements Runnable
{

    private static final Logger logger = Logger.getLogger(TalkingProxyThread.class.getName());

    public static final int BUFFER_SIZE = 1024;

    /**
     * The socket on which the client connection entered the system.
     */
    private Socket clientSocket = null;

    /**
     * The socket for this socket server to communicate with the server.
     */
    private Socket serverSocket = null;

    /**
     * The communication listener to report to.
     */
    private final SocketListener listener;

    /**
     * The conversation that needs to record everything.
     */
    private final Conversation conversation;

    /**
     * <p>Construction.</p><p><b>NOTE:</b> It is allowed for the serverSocket to be 
     * null. In this case the messages from the client are not forwarded to the
     * server, but they are recorded in the conversation.</p>
     * @param clientSocket the socket of the client
     * @param serverSocket the socket of the server
     * @param listener the listener to report to
     * @param conversation the conversation to record
     */
    public TalkingProxyThread(Socket clientSocket, Socket serverSocket, SocketListener listener, Conversation conversation)
    {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
        this.listener = listener;
        this.conversation = conversation;
    }

    /**
     * Translate the message as a human readable array of bytes. 
     * @param msg the original message
     * @param length the length of the message
     * @return a String showing the bytes, for example "0x0b 0x7a 0x51".
     */
    public String msgAsString(char[] msg, int length)
    {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < length; i++)
        {
            char b = msg[i];
            // FIXME: autowidening, if the msb is 1, the hex string becomes 0xffffffXX
            sb.append("0x").append(Integer.toHexString(b)).append(' ');
        }
        return sb.toString();
    }

    @Override
    public void run()
    {
        logger.log(Level.FINE, "run()");
        try (
                BufferedReader client_in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                PrintWriter server_out = serverSocket == null ? null : 
                        new PrintWriter(serverSocket.getOutputStream(), true);)
        {
            char[] buffer = new char[BUFFER_SIZE];

            // repeat until closed
            int numbers = 0;
            while (numbers != -1)
            {
                // retrieve stuff from client,
                // send it onwards to the server
                numbers = client_in.read(buffer);
                if (numbers > 0)
                {
                    logger.log(Level.FINE, ">{0}", new String(buffer, 0, numbers));
                    logger.log(Level.FINE, ">{0}", msgAsString(buffer, numbers));
                    conversation.addMessage(new Message(TransportEnum.CLIENT, buffer, numbers));
                    if (server_out != null)
                    {
                        logger.fine("Writing to server");
                        server_out.write(buffer, 0, numbers);
                        logger.fine("Flushing server");
                        server_out.flush();
                    }
                }
            }
            conversation.addMessage(new Message(TransportEnum.CLIENT_CLOSED_CONNECTION));
            logger.fine("client closed");
                        } catch (IOException ex)
                        {
                            logger.log(Level.SEVERE,  ex.getMessage(), ex);
                        } finally
                        {
                            try
                            {
                                if (serverSocket != null && !serverSocket.isClosed())
                                {
                                    serverSocket.shutdownOutput();
                                    serverSocket.close();
                                }
                                if (!clientSocket.isClosed())
                                {
                                    clientSocket.shutdownOutput();
                                    clientSocket.close();
                                }
                            } catch (IOException ex)
                            {
                                logger.log(Level.SEVERE,  ex.getMessage(), ex);
                            }
                        }

                        listener.communication(conversation);
    }

}
