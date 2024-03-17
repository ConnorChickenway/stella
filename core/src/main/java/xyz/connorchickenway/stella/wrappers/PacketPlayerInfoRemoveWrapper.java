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
import java.util.List;
import java.util.UUID;

import static xyz.connorchickenway.stella.util.NMSHelper.getNMSPackageName;
import static xyz.connorchickenway.stella.util.NMSVersion.isMajor;
import static xyz.connorchickenway.stella.util.ReflectionHelper.getClassForName;
import static xyz.connorchickenway.stella.util.ReflectionHelper.invokeConstructor;

public class PacketPlayerInfoRemoveWrapper implements PacketWrapper {

    private final Object packet;

    public PacketPlayerInfoRemoveWrapper(List<UUID> list) {
        packet = invokeConstructor(CONSTRUCTOR, list);
    }

    @Override
    public Object getPacket() {
        return packet;
    }

    private static Constructor<?> CONSTRUCTOR;

    static {
        if (isMajor()) {
            try {
                Class<?> PACKET_CLASS = getClassForName(getNMSPackageName() +
                        ".network.protocol.game.ClientboundPlayerInfoRemovePacket");
                CONSTRUCTOR = PACKET_CLASS.getConstructor(List.class);
            }catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
