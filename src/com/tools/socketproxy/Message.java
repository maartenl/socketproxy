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

/**
 *
 * @author maartenl
 */
public class Message {

    private final char[] message;
    
    private final TransportEnum transport;

    /**
     *
     * @param transport
     * @param message
     * @param size
     */
    public Message(@Nonnull TransportEnum transport, @Nonnull char[] message, int size) {
        this.message = Arrays.copyOf(message, size);
        this.transport = transport;
    }

    Message(TransportEnum transport)
    {
        this.transport = transport;
        this.message = null;
    }

    /**
     * @return the message
     */
    public @Nonnull char[] getMessage() {
        return message;
    }

    /**
     * @return the transport
     */
    public @Nonnull TransportEnum getTransport() {
        return transport;
    }
    
    
            
}
