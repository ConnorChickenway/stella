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

package xyz.connorchickenway.stella.wrappers;

import xyz.connorchickenway.stella.util.NMSVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static xyz.connorchickenway.stella.util.NMSHelper.*;
import static xyz.connorchickenway.stella.util.NMSVersion.*;
import static xyz.connorchickenway.stella.util.ReflectionHelper.*;

public class PacketScoreboardTeamWrapper implements PacketWrapper {

    private final Object packet;
    //1.17> Parameters
    private Object parameters;

    public PacketScoreboardTeamWrapper(String name, boolean create) {
        int mode = create ? 0 : 2;
        if (NMSVersion.isRemappedVersion()) {
            parameters = newInstance(PARAMETERS_CLASS);
            set("a", parameters, newChatComponent(""));
            set("d", parameters, "always");
            set("e", parameters, "always");
            set("f", parameters, CHAT_RESET_FORMAT);
            set("g", parameters, 0);
            packet = invokeConstructor(PACKET_CONSTRUCTOR, name, mode,
                    Optional.of(parameters), mode != 0 ? Collections.emptyList() : Collections.singletonList(name));
        } else {
            this.packet = invokeConstructor(PACKET_CONSTRUCTOR);
            set(TEAM_NAME, packet, name);
            set(ACTION, packet, mode);
            if (mode != 0) return;
            invokeMethod(ADD, get(GET_ENTRIES_COLLECTION, packet), name);
        }
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            prefix = "";
        set(PREFIX, isRemappedVersion() ? parameters : packet,
                compareIsBelow(v1_8_R3) ? prefix : newChatComponent(prefix));
    }

    public void setSuffix(String suffix) {
        if (suffix == null)
            suffix = "";
        set(SUFFIX, isRemappedVersion() ? parameters : packet,
                compareIsBelow(v1_8_R3) ? suffix : newChatComponent(suffix));
    }

    @Override
    public Object getPacket() {
        return packet;
    }

    private static final Constructor<?> PACKET_CONSTRUCTOR;
    private static Class<?> PARAMETERS_CLASS;
    private static Field TEAM_NAME, ACTION, GET_ENTRIES_COLLECTION;
    private static final Field PREFIX, SUFFIX;
    private static Object CHAT_RESET_FORMAT;

    static {
        try {
            Class<?> PACKET_TEAM_CLASS = Class.forName((isRemappedVersion() ?
                    getNMSPackageName() + ".network.protocol.game" :
                    getLegacyNMSPackageName()) +
                    ".PacketPlayOutScoreboardTeam");
            if (NMSVersion.isRemappedVersion()) {
                PACKET_CONSTRUCTOR = PACKET_TEAM_CLASS.getDeclaredConstructor(
                        String.class,
                        int.class,
                        Optional.class,
                        Collection.class
                );
                PACKET_CONSTRUCTOR.setAccessible(true);
                PARAMETERS_CLASS = Class.forName(PACKET_TEAM_CLASS.getName() + "$b");
                PREFIX = getDeclaredField(PARAMETERS_CLASS, "b");
                SUFFIX = getDeclaredField(PARAMETERS_CLASS, "c");
                Class<?> enumChatFormatClass = Class.forName(getNMSPackageName() + ".EnumChatFormat");
                CHAT_RESET_FORMAT = enumChatFormatClass.getField("v").get(null);
            }else {
                PACKET_CONSTRUCTOR = PACKET_TEAM_CLASS.getConstructor();
                TEAM_NAME = getDeclaredField(PACKET_TEAM_CLASS, "a");
                ACTION = getDeclaredField(PACKET_TEAM_CLASS, is1_7() ? "f" : compareIsBelow(v1_8_R3) ? "h" : "i");
                PREFIX = getDeclaredField(PACKET_TEAM_CLASS, "c");
                SUFFIX = getDeclaredField(PACKET_TEAM_CLASS, "d");
                GET_ENTRIES_COLLECTION = getDeclaredField(PACKET_TEAM_CLASS, is1_7() ? "e" : compareIsBelow(v1_8_R3) ? "g" : "h");
            }
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
