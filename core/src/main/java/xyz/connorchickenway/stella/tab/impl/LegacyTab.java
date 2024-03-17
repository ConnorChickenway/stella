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

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.modifier.TabUpdate;
import xyz.connorchickenway.stella.tab.skin.Skin;
import xyz.connorchickenway.stella.util.NMSHelper;
import xyz.connorchickenway.stella.wrappers.PacketPlayerInfoWrapper;
import xyz.connorchickenway.stella.wrappers.legacy.PacketScoreboardTeamWrapper;

import java.util.UUID;

import static xyz.connorchickenway.stella.util.NMSVersion.*;

public class LegacyTab extends PlayerTab {

    private final int protocolVersion;
    private final int divisor;
    private final boolean isMajor;

    //Class for 1.7.10 servers
    public LegacyTab(Player player, int protocolVersion) {
        super(player, new TabEntry[protocolVersion >= 47 ? WIDTH : WIDTH - 1][HEIGHT]);
        this.protocolVersion = protocolVersion;
        this.isMajor = protocolVersion >= 47;
        this.divisor = isMajor ? 20 : 3;
    }

    @Override
    public void init(TabModifier tabModifier) {
        for (int index = 0; index < (isMajor ? 80 : 60); index++) {
            if (!player.isOnline()) break;
            final int x = getX(index);
            final int y = getY(index);
            TabEntry tabEntry = entries[x][y] = buildEntry(x, y, tabModifier).initName();
            if (!tabEntry.hasSkin()) {
                tabEntry.setSkin(Skin.DEFAULT);
            }
            PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
            GameProfile gameProfile = isMajor ? createProfile(tabEntry) : null;
            final String entryText = tabEntry.getText(),
                    entryName = protocolVersion <= 5 ? NAMES[x][y] : null;
            wrapper.addProtocolHackEntry(
                    gameProfile,
                    isMajor ? entryText : entryName,
                    tabEntry.getPing()
            );
            wrapper.addAction(PacketPlayerInfoWrapper.Action.ADD_PLAYER);
            wrapper.sendPacket(player);
            if (protocolVersion <= 5)
            {
                PacketScoreboardTeamWrapper teamWrapper = new PacketScoreboardTeamWrapper(entryName, true);
                teamWrapper.addEntry(entryName);
                final String txt = prefixAndSuffix(tabEntry.getText());
                final String[] split = txt.split(":;:");
                if (split.length > 1) {
                    teamWrapper.setSuffix(split[1]);
                }
                teamWrapper.setPrefix(split[0]);
                tabEntry.setText(txt);
                teamWrapper.sendPacket(player);
            }
        }
        if (protocolVersion <= 5)
        {
            for (Player online : Bukkit.getOnlinePlayers())
            {
                NMSHelper.sendPacket(player, NMSHelper.removePlayerPacket(online));
            }
        }
        creating.set(false);
    }

    @Override
    public void update(TabUpdate tabUpdate) {
        final boolean is1_7 = protocolVersion <= 5;
        for (TabEntry updateEntry : tabUpdate.update(player)) {
            if (!player.isOnline()) break;
            if (is1_7 && updateEntry.getX() >= 3) continue;
            final TabEntry tabEntry = getEntryByPosition(updateEntry.getX(), updateEntry.getY());
            if (updateEntry.hasSkin() && protocolVersion >= 47) {
                Skin updateSkin = updateEntry.getSkin();
                if (!updateSkin.equals(tabEntry.getSkin())) {
                    tabEntry.setSkin(updateSkin);
                    final int ping = updateEntry.getPing();
                    if (ping != tabEntry.getPing()) {
                        tabEntry.setPing(ping);
                    }
                    final String text = updateEntry.getText();
                    if (!text.equals(tabEntry.getText())) {
                        tabEntry.setText(text);
                    }
                    PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
                    wrapper.addAction(PacketPlayerInfoWrapper.Action.ADD_PLAYER);
                    wrapper.addProtocolHackEntry(
                            createProfile(tabEntry),
                            text,
                            ping
                    );
                    wrapper.sendPacket(player);
                    return;
                }
            }
            final String entryName = is1_7 ? NAMES[updateEntry.getX()][updateEntry.getY()] : null;
            final GameProfile gameProfile = entryName != null ? null :
                    createProfile(tabEntry.getId(), tabEntry.getEntryName());
            final int ping = updateEntry.getPing();
            if (ping != tabEntry.getPing()) {
                tabEntry.setPing(ping);
                PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
                wrapper.addAction(PacketPlayerInfoWrapper.Action.UPDATE_LATENCY);
                wrapper.addProtocolHackEntry(
                        gameProfile,
                        entryName,
                        ping
                );
                wrapper.sendPacket(player);
            }
            final String text = updateEntry.getText();
            if (is1_7) {
                String converted = prefixAndSuffix(text);
                if (converted.equals(tabEntry.getText())) return;
                tabEntry.setText(converted);
                final PacketScoreboardTeamWrapper team = new PacketScoreboardTeamWrapper(entryName, false);
                String[] split = text.split(":;:");
                if (split.length > 1) {
                    team.setSuffix(split[1]);
                }
                team.setPrefix(split[0]);
                team.sendPacket(player);
                return;
            }
            if (text.equals(tabEntry.getText())) return;
            tabEntry.setText(text);
            PacketPlayerInfoWrapper wrapper = new PacketPlayerInfoWrapper();
            wrapper.addAction(PacketPlayerInfoWrapper.Action.UPDATE_DISPLAY_NAME);
            wrapper.addProtocolHackEntry(gameProfile, text, null);
            wrapper.sendPacket(player);
        }
    }

    private GameProfile createProfile(TabEntry tabEntry) {
        return createProfile(tabEntry.getId(), tabEntry.getEntryName(), tabEntry.getSkin());
    }

    private GameProfile createProfile(UUID uuid, String entryName, Skin skin) {
        GameProfile gameProfile = new GameProfile(uuid, entryName);
        if (skin != null) {
            gameProfile.getProperties().put("textures", new Property("textures",
                    skin.getValue(),
                    skin.getSignature()));
        }
        return gameProfile;
    }

    private GameProfile createProfile(UUID uuid, String entryName) {
        return createProfile(uuid, entryName, null);
    }

    private int getX(int index) {
        return isMajor ? index / divisor : index % divisor;
    }

    private int getY(int index) {
        return isMajor ? index % divisor : index / divisor;
    }

    private static String prefixAndSuffix(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        if (text.length() <= 16) {
            stringBuilder.append(text);
        } else {
            int i = text.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
            String prefix = text.substring(0, i);
            String suffix = (i == 16 ? ChatColor.getLastColors(prefix) : "") + text.substring(i);
            suffix = suffix.length() > 16 ? suffix.substring(0, 16) : suffix;
            stringBuilder.append(prefix)
                    .append(":;:")
                    .append(suffix);
        }
        return stringBuilder.toString();
    }

    public static String[][] NAMES;

    static {
        if (is1_7()) {
            NAMES = new String[3][20];
            ChatColor[] colors = ChatColor.values();
            for (int i = 0; i < 60; i++) {
                int x = i % 3;
                int y = i / 3;
                NAMES[x][y] = String.valueOf(colors[x]) + colors[y] + ChatColor.RESET;
            }
        }
    }

}
