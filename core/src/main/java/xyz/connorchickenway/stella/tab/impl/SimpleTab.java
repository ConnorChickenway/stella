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

package xyz.connorchickenway.stella.tab.impl;

import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.modifier.TabUpdate;
import xyz.connorchickenway.stella.tab.skin.Skin;
import xyz.connorchickenway.stella.util.TabUpdateHelper;
import xyz.connorchickenway.stella.wrappers.GameProfileWrapper;
import xyz.connorchickenway.stella.wrappers.PacketPlayerInfoWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xyz.connorchickenway.stella.util.NMSVersion.*;

public class SimpleTab extends PlayerTab {

    //Class for 1.8.x - 1.20.x
    public SimpleTab(Player player) {
        super(player);
    }

    @Override
    public void init(TabModifier tabModifier) {
        List<Object> entries = new ArrayList<>();
        PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
        final int divisor = 20;
        for (int index = 0; index < 80; index++) {
            if (!player.isOnline()) break;
            final int x = index / divisor;
            final int y = index % divisor;
            TabEntry tabEntry = this.entries[x][y] = buildEntry(x, y, tabModifier).initName();
            if (!tabEntry.hasSkin())
                tabEntry.setSkin(Skin.DEFAULT);
            GameProfileWrapper gameProfile = GameProfileWrapper.getGameProfile(tabEntry);
            PacketPlayerInfoWrapper.PlayerInfoData playerInfoData = new PacketPlayerInfoWrapper.PlayerInfoData(
                    gameProfile,
                    tabEntry.getPing(),
                    tabEntry.getText()
            );
            entries.add(playerInfoData.getPlayerData());
        }
        wrapper.addEntries(entries);
        wrapper.addAction(PacketPlayerInfoWrapper.Action.ADD_PLAYER, PacketPlayerInfoWrapper.Action.UPDATE_DISPLAY_NAME,
                PacketPlayerInfoWrapper.Action.UPDATE_LATENCY, PacketPlayerInfoWrapper.Action.UPDATE_LISTED);
        wrapper.sendPacket(player);
        creating.set(false);
    }

    @Override
    public void update(TabUpdate tabUpdate) {
        TabUpdateHelper tabUpdateHelper = new TabUpdateHelper(player);
        for (TabEntry updateEntry : tabUpdate.update(player)) {
            if (!player.isOnline()) break;
            TabEntry tabEntry = getEntryByPosition(updateEntry.getX(), updateEntry.getY());
            if (updateEntry.hasSkin()) {
                final Skin updateSkin = updateEntry.getSkin();
                if (!updateSkin.equals(tabEntry.getSkin())) {
                    tabEntry.setSkin(updateSkin);
                    final int ping = updateEntry.getPing();
                    if (ping != tabEntry.getPing())
                        tabEntry.setPing(ping);
                    final String updateText = updateEntry.getText();
                    if (!updateText.equals(tabEntry.getText()))
                        tabEntry.setText(updateText);
                    GameProfileWrapper gameProfile = GameProfileWrapper.getGameProfile(tabEntry);
                    tabUpdateHelper.addSkinEntry(new PacketPlayerInfoWrapper.PlayerInfoData(
                            gameProfile,
                            tabEntry.getPing(),
                            tabEntry.getText()
                    ));
                    continue;
                }
            }
            final GameProfileWrapper gameProfile = isMajor() ? null :
                    GameProfileWrapper.getGameProfileWithoutSkin(tabEntry);
            final UUID uuid = gameProfile != null ? null : tabEntry.getId();
            final int ping = updateEntry.getPing();
            if (ping != tabEntry.getPing()) {
                tabEntry.setPing(ping);
                tabUpdateHelper.addPingEntry(new PacketPlayerInfoWrapper.PlayerInfoData(
                        uuid,
                        gameProfile,
                        ping,
                        null)
                );
            }
            final String text = updateEntry.getText();
            if (!text.equals(tabEntry.getText())) {
                tabEntry.setText(text);
                tabUpdateHelper.addNameEntry(new PacketPlayerInfoWrapper.PlayerInfoData(
                        uuid,
                        gameProfile,
                        ping,
                        text)
                );
            }
        }
        tabUpdateHelper.doMagic(player);
    }

}
