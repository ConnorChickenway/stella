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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.modifier.TabUpdate;
import xyz.connorchickenway.stella.tab.skin.Skin;
import xyz.connorchickenway.stella.util.NMSHelper;
import xyz.connorchickenway.stella.util.TabUpdateHelper;
import xyz.connorchickenway.stella.wrappers.GameProfileWrapper;
import xyz.connorchickenway.stella.wrappers.PacketPlayerInfoRemoveWrapper;
import xyz.connorchickenway.stella.wrappers.PacketPlayerInfoWrapper;
import xyz.connorchickenway.stella.wrappers.PacketScoreboardTeamWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xyz.connorchickenway.stella.util.NMSVersion.*;
import static xyz.connorchickenway.stella.tab.impl.LegacyTab.prefixAndSuffix;
import static xyz.connorchickenway.stella.tab.impl.LegacyTab.getEntryName;

public class SimpleTab extends PlayerTab {

    //Class for 1.8.x - 1.20.x or ViaRewind for 1.7 players
    public SimpleTab(Player player) {
        super(player);
    }

    @Override
    public void init(TabModifier tabModifier) {
        List<Object> entries = new ArrayList<>();
        PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
        if (NMSHelper.has1_7(player)) {
            for (int index = 0; index < 60; index++) {
                final int x = index % 3;
                final int y = index / 3;
                TabEntry tabEntry = this.entries[x][y] = buildEntry(x, y, tabModifier).initName();
                GameProfileWrapper gameProfile = GameProfileWrapper.getGameProfileWithoutSkin(tabEntry);
                final String entryName = getEntryName(x,  y);
                PacketScoreboardTeamWrapper teamWrapper = new PacketScoreboardTeamWrapper(entryName, true);
                final String txt = prefixAndSuffix(tabEntry.getText());
                final String[] split = txt.split(":;:");
                if (split.length > 1) {
                    teamWrapper.setSuffix(split[1]);
                }
                teamWrapper.setPrefix(split[0]);
                tabEntry.setText(txt);
                teamWrapper.sendPacket(player);
                PacketPlayerInfoWrapper.PlayerInfoData playerInfoData = new PacketPlayerInfoWrapper.PlayerInfoData(
                        gameProfile,
                        tabEntry.getPing(),
                        entryName
                );
                entries.add(playerInfoData.getPlayerData());
            }
            wrapper.addAction(PacketPlayerInfoWrapper.Action.ADD_PLAYER);
            wrapper.addEntries(entries);
            wrapper.sendPacket(player);
            entries = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isMajor()) {
                    entries.add(player.getUniqueId());
                }else {
                    PacketPlayerInfoWrapper.PlayerInfoData playerInfoData = new PacketPlayerInfoWrapper.PlayerInfoData(
                            GameProfileWrapper.getGameProfile(player.getUniqueId(), "", null),
                            0,
                            ""
                    );
                    entries.add(playerInfoData.getPlayerData());
                }
            }
            PacketPlayerInfoRemoveWrapper removeWrapper = new PacketPlayerInfoRemoveWrapper(entries);
            removeWrapper.sendPacket(player);
            creating.set(false);
            return;
        }
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
        if (NMSHelper.has1_7(player)) {
            List<Object> entries = new ArrayList<>();
            for (TabEntry updateEntry : tabUpdate.update(player)) {
                if (!player.isOnline()) break;
                if (updateEntry.getX() >= 3) continue;
                TabEntry tabEntry = getEntryByPosition(updateEntry.getX(), updateEntry.getY());
                final String entryName = getEntryName(updateEntry.getX(), updateEntry.getY());
                final int ping = updateEntry.getPing();
                if (ping != tabEntry.getPing()) {
                    tabEntry.setPing(ping);
                    GameProfileWrapper gameProfile = GameProfileWrapper.getGameProfile(tabEntry);
                    PacketPlayerInfoWrapper.PlayerInfoData playerInfoData = new PacketPlayerInfoWrapper.PlayerInfoData(
                            gameProfile,
                            tabEntry.getPing(),
                            entryName
                    );
                    entries.add(playerInfoData.getPlayerData());
                }
                String converted = prefixAndSuffix(updateEntry.getText());
                if (converted.equals(tabEntry.getText())) return;
                tabEntry.setText(converted);
                PacketScoreboardTeamWrapper teamWrapper = new PacketScoreboardTeamWrapper(entryName, false);
                String[] split = converted.split(":;:");
                if (split.length > 1) {
                    teamWrapper.setSuffix(split[1]);
                }
                teamWrapper.setPrefix(split[0]);
                teamWrapper.sendPacket(player);
            }
            if (entries.isEmpty()) return;
            PacketPlayerInfoWrapper infoWrapper = new PacketPlayerInfoWrapper();
            infoWrapper.addAction(PacketPlayerInfoWrapper.Action.UPDATE_LATENCY);
            infoWrapper.addEntries(entries);
            infoWrapper.sendPacket(player);
            return;
        }
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
