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
 *
 * @author maartenl
 */
public class Conversation
{

    private final List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
    
    private final String hostName;

    private final String hostAddress;

    public Conversation(String hostName, String hostAddress)
    {
        this.hostName = hostName;
        this.hostAddress = hostAddress;
    }

    public void addMessage(Message aMessage)
    {
        messages.add(aMessage);
    }
    
    /**
     * When iterating over this list, make sure to synchronize on it, as it
     * is accessed by multiple threads.
     * @return 
     * @see http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList%28java.util.List%29
     */
    public List<Message> getMessages()
    {
        return messages;
    }
    
    
    public String getHostName()
    {
        return hostName;
    }

    public String getHostAddress()
    {
        return hostAddress;
    }
    
}
