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

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A message from client to server or vice versa.
 *
 * @author maartenl
 */
public class Message
{

    private final char[] message;

    private final TransportEnum transport;

    /**
     * Initializes the message.
     *
     * @param transport who is sending to whom.
     * @param message the message that was sent.
     * @param size the size of the message
     */
    public Message(@Nonnull TransportEnum transport, @Nonnull char[] message, int size)
    {
        this.message = Arrays.copyOf(message, size);
        this.transport = transport;
    }

    /**
     * Empty message. Good for indicating if someone has closed the connection.
     *
     * @param transport for example
     * {@link TransportEnum#CLIENT_CLOSED_CONNECTION} or
     * {@link TransportEnum#SERVER_CLOSED_CONNECTION}.
     */
    public Message(TransportEnum transport)
    {
        this.transport = transport;
        this.message = null;
    }

    /**
     * The message sent.
     *
     * @return the message
     */
    public @Nullable
    char[] getMessage()
    {
        return message;
    }

    /**
     * Who is sending to whom.
     *
     * @return the transport indicates the originating party.
     */
    public @Nonnull
    TransportEnum getTransport()
    {
        return transport;
    }

}
