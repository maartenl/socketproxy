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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

/**
 * Just dumps the entire conversation to the System.out.
 * @author maartenl
 */
class FileSocketListener implements SocketListener {

  private static int conversationId = 0;

  public static synchronized int nextValue() {
    return ++conversationId;
  }

  @Override
  public void communication(Conversation conversation) {
    int id = FileSocketListener.nextValue();

    File outFile = new File("fromClient_" + id + ".txt");
    File inFile = new File("fromServer_" + id + ".txt");
    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(outFile.toPath(), StandardOpenOption.CREATE,
        StandardOpenOption.APPEND));
        OutputStream in = new BufferedOutputStream(Files.newOutputStream(inFile.toPath(), StandardOpenOption.CREATE,
            StandardOpenOption.APPEND))) {

      List<Message> list = conversation.getMessages();
      System.out.println("Client host:" + conversation.getHostName());
      System.out.println("Client address:" + conversation.getHostAddress());
      synchronized (list) {
        Iterator<Message> i = list.iterator(); // Must be in synchronized block
        while (i.hasNext()) {
          Message message = i.next();
          switch (message.getTransport()) {
            case CLIENT:
              System.out.println("received from client:{" + new String(message.getMessage()) + "}");
              byte receivedFromClient[] = new String(message.getMessage()).getBytes();
              out.write(receivedFromClient, 0, receivedFromClient.length);
              break;
            case SERVER:
              System.out.println("received from server:{" + new String(message.getMessage()) + "}");
              byte receivedFromServer[] = new String(message.getMessage()).getBytes();
              in.write(receivedFromServer, 0, receivedFromServer.length);
              break;
            case CLIENT_CLOSED_CONNECTION:
              System.out.println("client closed connection.");
              break;
            case SERVER_CLOSED_CONNECTION:
              System.out.println("server closed connection.");
              break;
            case CANNOT_CONNECT_TO_SERVER:
              System.out.println("connection to server could not be established.");
              break;
            default:
              throw new RuntimeException("unknown transport type : " + message.getTransport());
          }
        }
      }
    }
    catch (IOException x) {
      System.err.println(x);
    }
  }
}
