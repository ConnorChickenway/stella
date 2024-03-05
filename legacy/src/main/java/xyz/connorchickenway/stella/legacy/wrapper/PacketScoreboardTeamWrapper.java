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

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.legacy.util.ReflectionUtil;

import java.util.Collection;

public class PacketScoreboardTeamWrapper implements PacketWrapper<PacketPlayOutScoreboardTeam> {

    private final PacketPlayOutScoreboardTeam team;

    public PacketScoreboardTeamWrapper(boolean create, String teamName) {
        team = new PacketPlayOutScoreboardTeam();
        ReflectionUtil.set(team, "a", teamName);
        ReflectionUtil.set(team, "f", create ? 0 : 2);
    }


    public void addEntry(String entry) {
        Collection collection = (Collection) ReflectionUtil.get(team, "e");
        collection.add(entry);
    }

    public void setPrefix(String prefix) {
        ReflectionUtil.set(team, "c", prefix);
    }

    public void setSuffix(String suffix) {
        ReflectionUtil.set(team, "d", suffix);
    }


    @Override
    public PacketPlayOutScoreboardTeam get() {
        return team;
    }

    @Override
    public void send(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(team);
    }

}
