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

import javax.annotation.Nonnull;

/**
 * Listener that will receive the communication detected.
 * @author maartenl
 */
public interface SocketListener
{
    /**
     * Call back containing the entire conversation of one socket session.
     * @param conversation the entire conversation, including some general parameters
     * that apply to the entire conversation.
     */
    public void communication(@Nonnull Conversation conversation);

}
