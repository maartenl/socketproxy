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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Indicates an entire conversion between the client and the server. The
 * conversation is recorded by this socketproxy.
 * @author maartenl
 */
public class Conversation
{

    private final List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
    
    private final String hostName;

    private final String hostAddress;

    /**
     * Create/start a conversation.
     * @param hostName the name of the client
     * @param hostAddress the address of the client
     */
    public Conversation(String hostName, String hostAddress)
    {
        this.hostName = hostName;
        this.hostAddress = hostAddress;
    }

    /**
     * Adds a message from either the server (to the client or 
     * the client (to the server).
     * @param aMessage the message to record.
     */
    public void addMessage(Message aMessage)
    {
        messages.add(aMessage);
    }
    
    /**
     * The entire list of messages that occurred.
     * When iterating over this list, make sure to synchronize on it, as it
     * is accessed by multiple threads.
     * @return a synchronized List of Messages.
     * @see http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList%28java.util.List%29
     */
    public List<Message> getMessages()
    {
        return messages;
    }
    
    /**
     * Returns the host name of the client.
     * @return String containing the hostname.
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * Returns the host address of the client.
     * @return String containing the ip address, usually the standard 4 bytes
     * with the dot(.) separator.
     */
    public String getHostAddress()
    {
        return hostAddress;
    }
    
}
