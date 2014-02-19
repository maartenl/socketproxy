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
class SocketProxyThread extends Thread {

    private Socket socket = null;

    private final SocketListener listener;

    private final List<Message> messages = new ArrayList<>();

    private final String serverHost;

    private final int serverPort;

    SocketProxyThread(@Nonnull Socket socket, @Nonnull SocketListener listener, @Nonnull String serverHost, @Nonnull int serverPort) {
        super("SocketProxyThread");
        this.socket = socket;
        this.listener = listener;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try (
                Socket serverSocket = new Socket(serverHost, serverPort);
                PrintWriter server_out = new PrintWriter(serverSocket.getOutputStream(), true);
                BufferedReader server_in = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream()));) {

            try (
                    PrintWriter client_out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader client_in = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));) {
                String client_inputLine, client_outputLine;
                String server_inputLine, server_outputLine;

                // repeat until closed
                while (!socket.isClosed()) {
                    // retrieve stuff from client,
                    // send it onwards to the server
                    while ((client_inputLine = client_in.readLine()) != null) {
                        messages.add(new Message(TransportEnum.CLIENT, client_inputLine.getBytes()));
                        server_out.println(client_inputLine);
                        break;
                    }
                    // retrieve stuff from server
                    // send it onwards to the client
                    while ((server_inputLine = server_in.readLine()) != null) {
                        messages.add(new Message(TransportEnum.SERVER, server_inputLine.getBytes()));
                        client_out.println(server_inputLine);
                        break;
                    }
                }
                socket.close();
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(SocketProxyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketProxyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        listener.communication(messages);
    }

}
