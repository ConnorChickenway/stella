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

package xyz.connorchickenway.stella.tab.entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.Tab;
import xyz.connorchickenway.stella.tab.skin.Skin;

import java.util.UUID;
import java.util.function.Function;

public class TabEntry {

    private final int x, y;
    private String entryName;
    private UUID uuid;
    private Skin skin;
    private int ping;
    private String text;

    TabEntry(int x, int y, String text, Skin skin, int ping) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.skin = skin;
        this.ping = ping;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public UUID getId() {
        return uuid;
    }

    public String getEntryName() {
        return entryName;
    }

    public Skin getSkin() {
        return skin;
    }

    public boolean hasSkin() {
        return skin != null;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public TabEntry initName() {
        entryName = " " + String.format("%014d", x * 20 + y) + " ";
        this.uuid = UUID.nameUUIDFromBytes(entryName.getBytes());
        return this;
    }
    public static class TabEntryBuilder {

        private String text;
        private Skin skin;
        private Integer x,
                y,
                ping;

        private TabEntryBuilder() {}

        public TabEntryBuilder x(int x) {
            if (x < 0 || x >= Tab.WIDTH) {
                throw  new IndexOutOfBoundsException();
            }
            this.x = x;
            return this;
        }

        public TabEntryBuilder y(int y) {
            if (y < 0 || y >= Tab.HEIGHT) {
                throw new IndexOutOfBoundsException();
            }
            this.y = y;
            return this;
        }

        public TabEntryBuilder ping(int ping) {
            this.ping = ping;
            return this;
        }

        public TabEntryBuilder skin(Skin skin) {
            this.skin = skin;
            return this;
        }

        public TabEntryBuilder text(String text) {
            this.text = text;
            return this;
        }

        public Integer getX() {
            return x;
        }

        public Integer getY() {
            return y;
        }

        public TabEntry build() {
            if (x == null || y == null) {
                throw new NullPointerException("You need to set X and Y variables");
            }
            String text = this.text != null ? this.text : "";
            return new TabEntry(
                    x,
                    y,
                    ChatColor.translateAlternateColorCodes('&', text),
                    skin != null ? skin : null,
                    ping != null ? ping : 0);
        }

        public static TabEntryBuilder createBlankEntry(int x, int y) {
            return builder()
                    .text("")
                    .skin(Skin.DEFAULT)
                    .x(x)
                    .y(y)
                    .ping(0);
        }

    }

    public static TabEntryBuilder builder() {
        return new TabEntryBuilder();
    }

}
