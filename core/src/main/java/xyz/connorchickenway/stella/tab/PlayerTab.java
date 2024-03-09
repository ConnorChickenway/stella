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

package xyz.connorchickenway.stella.tab;

import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PlayerTab implements Tab {

    protected final Player player;
    protected final TabEntry[][] entries;
    protected final AtomicBoolean creating;

    public PlayerTab(Player player, TabEntry[][] entries) {
        this.player = player;
        this.entries = entries;
        this.creating = new AtomicBoolean(true);
    }

    public PlayerTab(Player player) {
        this(player, new TabEntry[WIDTH][HEIGHT]);
    }

    @Override
    public boolean isCreating() {
        return creating.get();
    }

    @Override
    public TabEntry buildEntry(int x, int y, TabModifier tabModifier) {
        TabEntry.TabEntryBuilder entry = tabModifier.getTabEntryInitByPosition(x, y);
        if (entry == null)
            entry = TabEntry.TabEntryBuilder.createBlankEntry(x, y);
        return entries[x][y] = entry.build();
    }

    @Override
    public TabEntry getEntryByPosition(int x, int y) {
        return entries[x][y];
    }

    public Player getPlayer() {
        return player;
    }
}
