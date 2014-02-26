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

import java.util.List;

/**
 *
 * @author maartenl
 */
class SimpleSocketListener implements SocketListener
{

    public SimpleSocketListener()
    {
    }

    @Override
    public void communication(List<Message> messages)
    {
        for (Message message : messages)
        {
            switch (message.getTransport())
            {
                case CLIENT:
                    System.out.println("received from client:{" + new String(message.getMessage()) + "}");
                    break;
                case SERVER:
                    System.out.println("received from server:{" + new String(message.getMessage()) + "}");
                    break;
                case CLIENT_CLOSED_CONNECTION:
                    System.out.println("client closed connection.");
                    break;
                case SERVER_CLOSED_CONNECTION:
                    System.out.println("server closed connection.");
                    break;
                default:
                    throw new RuntimeException("unknown transport type : " + message.getTransport());
            }

        }
    }

}
