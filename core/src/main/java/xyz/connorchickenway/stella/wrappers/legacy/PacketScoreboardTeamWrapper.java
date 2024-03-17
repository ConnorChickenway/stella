/*
 *     Stella - A tablist API
 *     Copyright (C) 2024  ConnorChickenway
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package xyz.connorchickenway.stella.wrappers.legacy;

import xyz.connorchickenway.stella.util.NMSVersion;
import xyz.connorchickenway.stella.wrappers.PacketWrapper;

import java.lang.reflect.Field;

import static xyz.connorchickenway.stella.util.NMSHelper.getLegacyNMSPackageName;
import static xyz.connorchickenway.stella.util.ReflectionHelper.*;

public class PacketScoreboardTeamWrapper implements PacketWrapper {

    private final Object packet;

    public PacketScoreboardTeamWrapper(String teamName, boolean create) {
        packet = newInstance(PACKET_TEAM_CLASS);
        set(TEAM_NAME, packet, teamName);
        set(ACTION, packet, create ? 0 : 2);
    }

    public void addEntry(String entryName) {
        invokeMethod(ADD, get(GET_ENTRIES_COLLECTION, packet), entryName);
    }

    public void setPrefix(String prefix) {
        set(PREFIX, packet, prefix);
    }

    public void setSuffix(String suffix) {
        set(SUFFIX, packet, suffix);
    }

    @Override
    public Object getPacket() {
        return packet;
    }

    private static Class<?> PACKET_TEAM_CLASS;
    private static Field TEAM_NAME, ACTION, PREFIX, SUFFIX, GET_ENTRIES_COLLECTION;

    static {
        if (NMSVersion.is1_7()) {
            try {
                PACKET_TEAM_CLASS = Class.forName(getLegacyNMSPackageName() + ".PacketPlayOutScoreboardTeam");
                TEAM_NAME = getDeclaredField(PACKET_TEAM_CLASS, "a");
                ACTION = getDeclaredField(PACKET_TEAM_CLASS, "f");
                PREFIX = getDeclaredField(PACKET_TEAM_CLASS, "c");
                SUFFIX = getDeclaredField(PACKET_TEAM_CLASS, "d");
                GET_ENTRIES_COLLECTION = getDeclaredField(PACKET_TEAM_CLASS, "e");
            }catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
