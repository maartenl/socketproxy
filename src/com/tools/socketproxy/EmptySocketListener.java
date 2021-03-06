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

/**
 * For debugging purposes. If we're not interested in the contents,
 * but only want to see the logging.
 * @author maartenl
 */
class EmptySocketListener implements SocketListener
{

    @Override
    public void communication(Conversation conversation)
    {
        // nothing to see here, move along!
    }
}
