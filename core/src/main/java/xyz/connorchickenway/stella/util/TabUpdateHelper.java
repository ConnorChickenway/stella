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
import xyz.connorchickenway.stella.wrappers.PacketPlayerInfoRemoveWrapper;
import xyz.connorchickenway.stella.wrappers.PacketPlayerInfoWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabUpdateHelper {
    
    private final List<Object> skinEntries, namesEntries, pingEntries;
    private List<Object> removePlayers;

    public TabUpdateHelper(Player player) {
        final int protocolVersion = NMSHelper.getProtocolVersion(player);
        this.skinEntries = new ArrayList<>();
        this.namesEntries = new ArrayList<>();
        this.pingEntries = new ArrayList<>();
        if (NMSVersion.isMajor()) {
            if (protocolVersion != -1 && protocolVersion != 761)
                return;
            removePlayers = new ArrayList<>();
        } else if (protocolVersion >= 761)
            removePlayers = new ArrayList<>();
    }

    public void addSkinEntry(PacketPlayerInfoWrapper.PlayerInfoData playerInfoData) {
        skinEntries.add(playerInfoData.getPlayerData());
        if (removePlayers != null) {
            if (NMSVersion.isMajor())
                removePlayers.add(playerInfoData.getId());
            else
                removePlayers.add(playerInfoData.getPlayerData());
        }
    }

    public void addNameEntry(PacketPlayerInfoWrapper.PlayerInfoData playerInfoData) {
        namesEntries.add(playerInfoData.getPlayerData());
    }

    public void addPingEntry(PacketPlayerInfoWrapper.PlayerInfoData playerInfoData) {
        pingEntries.add(playerInfoData.getPlayerData());
    }

    public boolean hasSkinEntries() {
        return !skinEntries.isEmpty();
    }

    public boolean hasNamesEntries() {
        return !namesEntries.isEmpty();
    }

    public boolean hasPingEntries() {
        return !pingEntries.isEmpty();
    }

    public void doMagic(Player player) {
        if (hasSkinEntries()) {
            PacketPlayerInfoRemoveWrapper removeWrapper = null;
            if (removePlayers != null) {
                removeWrapper = new PacketPlayerInfoRemoveWrapper(removePlayers);
            }
            PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
            wrapper.addEntries(skinEntries);
            wrapper.addAction(PacketPlayerInfoWrapper.Action.ADD_PLAYER, PacketPlayerInfoWrapper.Action.UPDATE_DISPLAY_NAME,
                    PacketPlayerInfoWrapper.Action.UPDATE_LATENCY, PacketPlayerInfoWrapper.Action.UPDATE_LISTED);
            if (removeWrapper != null) {
                removeWrapper.sendPacket(player);
            }
            wrapper.sendPacket(player);
        }
        if (hasNamesEntries()) {
            PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
            wrapper.addEntries(namesEntries);
            wrapper.addAction(PacketPlayerInfoWrapper.Action.UPDATE_DISPLAY_NAME);
            wrapper.sendPacket(player);
        }
        if (hasPingEntries()) {
            PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
            wrapper.addEntries(pingEntries);
            wrapper.addAction(PacketPlayerInfoWrapper.Action.UPDATE_LATENCY);
            wrapper.sendPacket(player);
        }
    }

}
