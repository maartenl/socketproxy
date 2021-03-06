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
 * The server is talking to us, and we're sending it onto the client.
 * @see TalkingProxyThread
 * @author maartenl
 */
class TalkingServerThread implements Runnable
{

    private static final Logger logger = Logger.getLogger(TalkingServerThread.class.getName());

    public static final int BUFFER_SIZE = 1024;

    /**
     * The conversation that needs to record everything.
     */
    private final Conversation conversation;

    /**
     * The socket on which the client connection entered the system.
     */
    private Socket clientSocket = null;

    /**
     * The socket for this socket server to communicate with the server.
     */
    private Socket serverSocket = null;

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

    /**
     * Construction.
     * @param clientSocket the socket of the client
     * @param serverSocket the socket of the server
     * @param conversation the conversation to record
     */
    public TalkingServerThread(Socket clientSocket, Socket serverSocket, Conversation conversation)
    {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
        this.conversation = conversation;
    }

    @Override
    public void run()
    {
        logger.log(Level.FINE, "run()");
        try (PrintWriter client_out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader server_in = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream()));)
        {
            char[] buffer = new char[BUFFER_SIZE];

            // repeat until closed
            int numbers = 0;
            while (numbers != -1)
            {

                // retrieve stuff from server
                // send it onwards to the client
                numbers = server_in.read(buffer);
                if (numbers > 0)
                {
                    logger.log(Level.FINE, "<{0}", new String(buffer, 0, numbers));
                    logger.log(Level.FINE, "<{0}", msgAsString(buffer, numbers));
                    conversation.addMessage(new Message(TransportEnum.SERVER, buffer, numbers));
                    logger.fine("Writing to client");
                    client_out.write(buffer, 0, numbers);
                    logger.fine("Flushing client");
                    client_out.flush();
                }
            }
            conversation.addMessage(new Message(TransportEnum.SERVER_CLOSED_CONNECTION));
            logger.fine("server closed");
            if (!serverSocket.isClosed())
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

}
