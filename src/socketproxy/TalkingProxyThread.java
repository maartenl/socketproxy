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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 * The client is talking to us, and we're sending it onto the server.
 *
 * @author maartenl
 */
class TalkingProxyThread extends Thread
{

    private static final Logger logger = Logger.getLogger(TalkingProxyThread.class.getName());

    public static final int BUFFER_SIZE = 1024;

    /**
     * default timeout in milliseconds. 30 seconds outta do it.
     */
    private static final int SOCKET_TIMEOUT = 30000;

    /**
     * The socket on which the client connection entered the system.
     */
    private Socket clientSocket = null;
    private Socket serverSocket = null;

    private final SocketListener listener;

    private final List<Message> messages = new ArrayList<>();

    TalkingProxyThread(Socket clientSocket, Socket serverSocket, SocketListener listener)
    {
        super("TalkingProxyThread");
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;

        this.listener = listener;
    }

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
        logger.log(Level.FINE, "{0}.run()", this.getName());
        try (
                BufferedReader client_in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream())); PrintWriter server_out = new PrintWriter(serverSocket.getOutputStream(), true);)
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
                    messages.add(new Message(TransportEnum.CLIENT, buffer, numbers));
                    logger.fine("Writing to server");
                    server_out.write(buffer, 0, numbers);
                    logger.fine("Flushing server");
                    server_out.flush();
                }
            }
            messages.add(new Message(TransportEnum.CLIENT_CLOSED_CONNECTION));
            logger.fine("client closed");

                        } catch (IOException ex)
                        {
                            logger.log(Level.SEVERE, null, ex);
                        } finally
                        {
                            try
                            {
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
                                Logger.getLogger(TalkingProxyThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        listener.communication(messages);
    }

}
