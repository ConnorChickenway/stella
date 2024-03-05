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

package xyz.connorchickenway.stella.tab.modifier;

import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TabModifier {

    private Map<Position, TabEntry.TabEntryBuilder> initMap;
    private TabUpdate tabUpdate;

    private TabModifier(TabInit tabInit) {
        if (tabInit != null) {
            this.initMap = new ConcurrentHashMap<>();
            List<TabEntry.TabEntryBuilder> init = tabInit.init();
            if (init != null) {
                init.stream()
                        .filter(entry -> entry.getX() != null && entry.getY() != null)
                        .filter(entry -> entry.getX() > 0 && entry.getX() < PlayerTab.WIDTH)
                        .filter(entry -> entry.getY() > 0 && entry.getY() < PlayerTab.HEIGHT)
                        .forEach(this::add);
            }
        }

    }

    public TabEntry.TabEntryBuilder getTabEntryInitByPosition(int x, int y) {
        return initMap.get(Position.of(x, y));
    }

    public TabUpdate getTabUpdate() {
        return tabUpdate;
    }

    private void add(TabEntry.TabEntryBuilder tabEntryBuilder) {
        initMap.put(Position
                .of(tabEntryBuilder.getX(),
                        tabEntryBuilder.getY()),
                tabEntryBuilder);
    }

    public static class TabModifierBuilder {

        private TabInit tabInit;
        private TabUpdate tabUpdate;


        private TabModifierBuilder() {}

        public TabModifierBuilder init(TabInit tabInit) {
            this.tabInit = tabInit;
            return this;
        }

        public TabModifierBuilder update(TabUpdate tabUpdate) {
            this.tabUpdate = tabUpdate;
            return this;
        }

        public TabModifier build() {
            TabModifier tabModifier = new TabModifier(tabInit);
            tabModifier.tabUpdate = this.tabUpdate;
            return tabModifier;
        }

    }

    private static class Position {
        private final int x, y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Position) {
                if (this == o) return true;
                Position tmpPosition = (Position) o;
                return this.x == tmpPosition.x && this.y == tmpPosition.y;
            }
            return false;
        }

        public static Position of(int x, int y) {
            return new Position(x, y);
        }

    }

    public static TabModifierBuilder builder() {
        return new TabModifierBuilder();
    }

}
