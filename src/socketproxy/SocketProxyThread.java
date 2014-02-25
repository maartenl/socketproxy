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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 *
 * @author maartenl
 */
class SocketProxyThread extends Thread
{

    private static final Logger logger = Logger.getLogger(SocketProxyThread.class.getName());

    public static final int BUFFER_SIZE = 1024;

    /**
     * default timeout in milliseconds. 30 seconds outta do it.
     */
    private static final int SOCKET_TIMEOUT = 30000;

    /**
     * The socket on which the client connection entered the system.
     */
    private Socket socket = null;

    private final SocketListener listener;

    private final List<Message> messages = new ArrayList<>();

    private final String serverHost;

    private final int serverPort;

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

    SocketProxyThread(@Nonnull Socket socket, @Nonnull SocketListener listener, @Nonnull String serverHost, @Nonnull int serverPort)
    {
        super("SocketProxyThread");
        this.socket = socket;
        this.listener = listener;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void run()
    {
        logger.fine("Connection accepted");
        try (
                Socket serverSocket = new Socket(serverHost, serverPort);
                PrintWriter server_out = new PrintWriter(serverSocket.getOutputStream(), true);
                BufferedReader server_in = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream()));)
        {

            try (
                    PrintWriter client_out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader client_in = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));)
            {
                serverSocket.setSoTimeout(SOCKET_TIMEOUT);
                serverSocket.setKeepAlive(false);
                serverSocket.setSoLinger(false, 0);
                char[] client_inputLine = new char[BUFFER_SIZE];
                char[] client_outputLine = new char[BUFFER_SIZE];
                char[] server_inputLine = new char[BUFFER_SIZE];
                char[] server_outputLine = new char[BUFFER_SIZE];

                // repeat until closed
                while (true)
                {
                    // retrieve stuff from client,
                    // send it onwards to the server
                    if (client_in.ready())
                    {
                        int numbers = client_in.read(client_inputLine);
                        if (numbers > 0)
                        {
                            logger.fine(">" + new String(client_inputLine, 0, numbers));
                            logger.fine(">" + msgAsString(client_inputLine, numbers));
                            messages.add(new Message(TransportEnum.CLIENT, client_inputLine, numbers));
                            logger.fine("Writing to server");
                            server_out.write(client_inputLine, 0, numbers);
                            logger.fine("Flushing server");
                            server_out.flush();
                        }
                    }

                    // retrieve stuff from server
                    // send it onwards to the client
                    if (server_in.ready())
                    {
                        int numbers = server_in.read(server_inputLine);
                        if (numbers > 0)
                        {
                            logger.fine("<" + new String(server_inputLine, 0, numbers));
                            logger.fine(">" + msgAsString(server_inputLine, numbers));
                            messages.add(new Message(TransportEnum.SERVER, server_inputLine, numbers));
                            logger.fine("Writing to client");
                            client_out.write(server_inputLine, 0, numbers);
                            logger.fine("Flushing client");
                            client_out.flush();
                        }
                    }
                    if (socket.isClosed())
                    {
                        messages.add(new Message(TransportEnum.CLIENT_CLOSED_CONNECTION));
                        logger.fine("client closed");

                        break;
                    }
                    if (serverSocket.isClosed())
                    {
                        messages.add(new Message(TransportEnum.SERVER_CLOSED_CONNECTION));
                        logger.fine("server closed");
                        break;
                    }
                }
                socket.shutdownOutput();
                serverSocket.shutdownOutput();
                socket.close();
                serverSocket.close();
            } catch (IOException ex)
            {
                logger.log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }

        listener.communication(messages);
    }

}
