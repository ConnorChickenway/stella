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

package xyz.connorchickenway.stella.util;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static xyz.connorchickenway.stella.util.NMSVersion.*;
import static xyz.connorchickenway.stella.util.ReflectionHelper.*;

public class NMSHelper {

    private NMSHelper() {}

    public static String getLegacyNMSPackageName() {
        return getNMSPackageName() + ".server." + SERVER_VERSION;
    }

    public static String getNMSPackageName() {
        return "net.minecraft";
    }

    public static String getCraftBukkitPackageName() {
        return "org.bukkit.craftbukkit." + SERVER_VERSION;
    }

    private static final Method GET_HANDLE, SEND_PACKET;
    private static final Field PLAYER_CONNECTION;
    private static Method ALLOCATE_INSTANCE, GET_VERSION;
    //1.7.10 method & field for get protocol version
    private static Field NETWORK_MANAGER;
    private static Object UNSAFE;
    //1.7.10 method for remove players in tablist
    private static Method _1_7_REMOVE_PLAYER;


    static {
        if (SERVER_VERSION == null) {
            throw new NullPointerException("Something is wrong with your server.!");
        }
        try {
            //If SERVER_VERSION is 1_7_10, then verify if it is ProtocolHack Version
            if (is1_7()) {
                Class.forName("org.spigotmc.ProtocolInjector");
            }
            //CraftPlayer
            Class<?> craftPlayer = Class.forName(getCraftBukkitPackageName() + ".entity.CraftPlayer");
            GET_HANDLE = craftPlayer.getMethod("getHandle");
            //EntityPlayer
            Class<?> entityPlayer = Class.forName(isRemappedVersion() ?
                    getNMSPackageName() + ".server.level.EntityPlayer" :
                    getLegacyNMSPackageName() + ".EntityPlayer"
            );
            //PlayerConnection
            PLAYER_CONNECTION = entityPlayer.getField(compare(v1_20_R1) ?
                    "c" : isRemappedVersion() ?
                    "b":
                    "playerConnection");
            //SEND_PACKET
            Class<?> plConnectionClass = PLAYER_CONNECTION.getType();
            SEND_PACKET = compareIsBelow(v1_16_R3) ?
                    plConnectionClass.getMethod("sendPacket", Class.forName(getLegacyNMSPackageName() + ".Packet")) :
                    Arrays.stream(compare(v1_20_R2) ? plConnectionClass.getSuperclass().getMethods() :
                                    plConnectionClass.getMethods())
                            .filter(method -> method.getParameterCount() == 1 && method.getParameters()[0].getType()
                                    .getName().equals("net.minecraft.network.protocol.Packet"))
                            .findFirst().orElseThrow(NoSuchMethodError::new);
            //getting player protocol version
            if (is1_7()) {
                //GET PROTOCOL VERSION
                NETWORK_MANAGER = plConnectionClass.getField("networkManager");
                Class<?> networkManagerClass = NETWORK_MANAGER.getType();
                GET_VERSION = networkManagerClass.getMethod("getVersion");
                //REMOVE PLAYER
                _1_7_REMOVE_PLAYER = Class.forName(getLegacyNMSPackageName() + ".PacketPlayOutPlayerInfo")
                        .getMethod("removePlayer", GET_HANDLE.getReturnType());
            }
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (isRemappedVersion()) {
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field field = unsafeClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                UNSAFE = field.get(null);
                ALLOCATE_INSTANCE = unsafeClass.getMethod("allocateInstance", Class.class);
            }catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static Object allocateInstance(Class<?> cls) {
        return invokeMethod(ALLOCATE_INSTANCE, UNSAFE, cls);
    }

    public static void sendPacket(Player player, Object packet) {
        Object nmsPlayer = invokeMethod(GET_HANDLE, player);
        invokeMethod(SEND_PACKET, get(PLAYER_CONNECTION, nmsPlayer), packet);
    }

    public static Object removePlayerPacket(Player player) {
        return invokeStaticMethod(_1_7_REMOVE_PLAYER, invokeMethod(GET_HANDLE, player));
    }

    public static int getProtocolVersion(Player player) {
        if (is1_7()) {
            try {
                Object nmsPlayer = GET_HANDLE.invoke(player);
                Object playerConnection = PLAYER_CONNECTION.get(nmsPlayer);
                return (int) GET_VERSION.invoke(NETWORK_MANAGER.get(playerConnection));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        if (ViaProtocol.hasViaVersion()) {
            return ViaProtocol.getProtocolVersion(player);
        }
        //a random number;
        return -1;
    }

}