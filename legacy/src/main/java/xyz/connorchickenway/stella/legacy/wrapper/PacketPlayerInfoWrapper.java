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

package xyz.connorchickenway.stella.legacy.wrapper;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.legacy.util.ReflectionUtil;

public class PacketPlayerInfoWrapper implements PacketWrapper<PacketPlayOutPlayerInfo> {

    private final PacketPlayOutPlayerInfo packet;

    public PacketPlayerInfoWrapper(PlayerInfoAction action) {
        this.packet = new PacketPlayOutPlayerInfo();
        this.setAction(action);
    }

    private void setAction(PlayerInfoAction action) {
        ReflectionUtil.set(this.packet, "action", action.ordinal());
    }

    public void setText(String username) {
        ReflectionUtil.set(this.packet, "username", username);
    }

    public void setPing(int ping) {
        ReflectionUtil.set(this.packet, "ping", ping);
    }

    public void setProfile(GameProfile gameProfile) {
        ReflectionUtil.set(this.packet,"player", gameProfile);
    }

    @Override
    public PacketPlayOutPlayerInfo get() {
        return packet;
    }

    @Override
    public void send(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(this.packet);
    }

    public enum PlayerInfoAction {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;
    }

    public static void removePlayerInfo(Player player, Player remove) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        Packet packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)remove).getHandle());
        entityPlayer.playerConnection.sendPacket(packet);
    }

}
