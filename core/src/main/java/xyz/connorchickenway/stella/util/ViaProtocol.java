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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ViaProtocol {

    private static ViaAPI<?> viaAPI;

    private ViaProtocol(){}

    public static boolean hasViaVersion() {
        return viaAPI != null;
    }

    public static int getProtocolVersion(Player player) {
        return viaAPI.getPlayerVersion(player.getUniqueId());
    }

    static {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion")) {
            viaAPI = Via.getAPI();
        }
    }


}
