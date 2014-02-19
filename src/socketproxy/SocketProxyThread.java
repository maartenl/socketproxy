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

/**
 *
 * @author maartenl
 */
class SocketProxyThread extends Thread {

    private Socket socket = null;
    
    private SocketListener listener;
    
    private final List<Message> messages = new ArrayList<>();

    public SocketProxyThread(Socket socket, SocketListener listener) {
        super("SocketProxyThread");
        this.socket = socket;
        this.listener = listener;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));) {
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                messages.add(new Message(TransportEnum.CLIENT, inputLine.getBytes()));
                break;
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketProxyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
