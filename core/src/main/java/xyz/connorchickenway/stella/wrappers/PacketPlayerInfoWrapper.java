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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

import static xyz.connorchickenway.stella.util.NMSHelper.*;
import static xyz.connorchickenway.stella.util.NMSVersion.*;
import static xyz.connorchickenway.stella.util.ReflectionHelper.*;

public class PacketPlayerInfoWrapper implements PacketWrapper {

    private final Object packet;

    public PacketPlayerInfoWrapper() {
        packet = isRemappedVersion() ? allocateInstance(PACKET_CLASS) :
                newInstance(PACKET_CLASS);
    }

    public void addAction(Action... action) {
        Object object;
        if (isMajor()) {
            object = invokeStaticMethod(ENUM_SET_NONE_OF, ENUM_CLASS);
            for (Action a : action)
                invokeMethod(ADD, object, a.action());
        } else {
            object = action[0].action();
        }
        set(ACTION, packet, object);
    }

    public void addEntries(Collection<Object> collection) {
        if (isMajor())
            set(ENTRIES, packet, collection);
        else
            invokeMethod(ADD_ALL, get(ENTRIES, packet), collection);
    }

    public void addProtocolHackEntry(Object gameProfile, String username, Integer ping) {
        if (gameProfile != null)
            set(GAME_PROFILE, packet, gameProfile);
        if (username != null)
            set(USERNAME, packet, username);
        if (ping != null)
            set(PING, packet, ping);
    }

    @Override
    public Object getPacket() {
        return packet;
    }

    public enum Action {

        ADD_PLAYER(0, "ADD_PLAYER", "a", "a"),
        UPDATE_LATENCY(2, "UPDATE_LATENCY", "c", "e"),
        UPDATE_DISPLAY_NAME(3, "UPDATE_DISPLAY_NAME", "d", "f"),
        //this was introduced in 1.19.3;
        UPDATE_LISTED("d"),
        //this has been removed in 1.19.3; they created new packet = ClientboundPlayerInfoRemovePacket;
        REMOVE_PLAYER(4, "REMOVE_PLAYER", "e", null);

        private final String legacy, spigotRemapped, major;
        private final int protocolHack;

        Action(String major) {
            this(-1, null, null, major);
        }

        Action(int protocolHack, String legacy, String spigotRemapped, String major) {
            this.protocolHack = protocolHack;
            this.legacy = legacy;
            this.spigotRemapped = spigotRemapped;
            this.major = major;
        }

        private String field() {
            return isMajor() ? major : isRemappedVersion() ? spigotRemapped : legacy;
        }

        public int getProtocolHack() {
            return protocolHack;
        }

        public Object action() {
            if (is1_7()) {
                return getProtocolHack();
            }
            return getStaticField(ENUM_CLASS, field());
        }

    }

    public static class PlayerInfoData {

        private final GameProfileWrapper gameProfile;
        private final int latency;
        private final Object displayName;
        private UUID uuid;

        public PlayerInfoData(GameProfileWrapper gameProfile, int latency, String displayName) {
            this.gameProfile = gameProfile;
            this.latency = latency;
            this.displayName = displayName != null ? newChatComponent(displayName) : null;
        }

        public PlayerInfoData(UUID uuid, GameProfileWrapper gameProfile, int latency, String displayName) {
            this(gameProfile, latency, displayName);
            this.uuid = uuid;
        }

        public UUID getId() {
            return gameProfile != null ? gameProfile.getId() : uuid;
        }

        public Object getPlayerData() {
            try {
                if (isMajor()) {
                    return PLAYER_INFO_DATA_CONSTRUCTOR.newInstance(
                            uuid != null ? uuid : gameProfile.getId(),
                            gameProfile.getGameProfile(),
                            true,
                            latency,
                            ENUM_GAME_MODE,
                            displayName,
                            null
                    );
                } else
                if (isRemappedVersion())
                    return PLAYER_INFO_DATA_CONSTRUCTOR.newInstance(
                            gameProfile.getGameProfile(),
                            latency,
                            ENUM_GAME_MODE,
                            displayName
                    );
                return PLAYER_INFO_DATA_CONSTRUCTOR.newInstance(
                        STATIC_PACKET,
                        gameProfile.getGameProfile(),
                        latency,
                        ENUM_GAME_MODE,
                        displayName
                );
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private static final Constructor<?> PLAYER_INFO_DATA_CONSTRUCTOR = PLAYER_INFO_DATA_CLASS.getConstructors()[0];

    }

    private static final Class<?> PACKET_CLASS;
    private static final Field ACTION, ENTRIES;
    private static Class<?> ENUM_CLASS;
    private static Class<?> PLAYER_INFO_DATA_CLASS;
    private static Object ENUM_GAME_MODE;
    //1.7.10 fields
    private static Field GAME_PROFILE, USERNAME, PING;
    //1.8.x - 1.16.x
    private static final Object STATIC_PACKET;

    static {
        try {
            PACKET_CLASS = Class.forName(isRemappedVersion() ?
                    getNMSPackageName() + ".network.protocol.game." +
                            (isMajor() ? "ClientboundPlayerInfoUpdatePacket" : "PacketPlayOutPlayerInfo") :
                    getLegacyNMSPackageName() + ".PacketPlayOutPlayerInfo");
            ACTION = getDeclaredField(PACKET_CLASS, is1_7() ? "action" : "a");
            ENTRIES = is1_7() ? null : getDeclaredField(PACKET_CLASS, "b");
            STATIC_PACKET = is1_7() || isMajor() ? null : PACKET_CLASS.newInstance();
            //PlayerInfoData Class
            if (compare(v1_8_R1)) {
                PLAYER_INFO_DATA_CLASS = Class.forName(PACKET_CLASS.getName() + "$" + (isMajor() ?
                        "b" :
                        "PlayerInfoData"));
                //EnumGameMode init
                Class<?> enumGameModeClass = Class.forName(isRemappedVersion() ?
                        getNMSPackageName() + ".world.level.EnumGamemode" :
                        getLegacyNMSPackageName() + (compare(v1_10_R1) ?
                                ".EnumGamemode" : (compare(v1_8_R2) ?
                                ".WorldSettings$EnumGamemode" : ".EnumGamemode")));
                ENUM_GAME_MODE = enumGameModeClass.getField(compareIsBelow(v1_16_R3) ?
                        "NOT_SET" : "a").get(null);
                ENUM_CLASS = getClassForName(PACKET_CLASS.getName() + "$" + (isMajor() ?
                        "a" : "EnumPlayerInfoAction"));
            }
            else {
                //
                GAME_PROFILE = getDeclaredField(PACKET_CLASS, "player");
                USERNAME = getDeclaredField(PACKET_CLASS, "username");
                PING = getDeclaredField(PACKET_CLASS, "ping");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}