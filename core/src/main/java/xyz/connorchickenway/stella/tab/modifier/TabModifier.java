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

    private Map<Integer, TabEntry.TabEntryBuilder> initMap;
    private TabUpdate tabUpdate;

    private TabModifier(TabInit tabInit) {
        if (tabInit != null) {
            this.initMap = new ConcurrentHashMap<>();
            List<TabEntry.TabEntryBuilder> init = tabInit.init();
            if (init != null) {
                init.stream()
                        .filter(entry -> entry.getX() != null && entry.getY() != null)
                        .filter(entry -> entry.getX() >= 0 && entry.getX() < PlayerTab.WIDTH)
                        .filter(entry -> entry.getY() >= 0 && entry.getY() < PlayerTab.HEIGHT)
                        .forEach(this::add);
            }
        }

    }

    public TabEntry.TabEntryBuilder getTabEntryInitByPosition(int x, int y) {
        return initMap.get(x * 20 + y);
    }

    public TabUpdate getTabUpdate() {
        return tabUpdate;
    }

    private void add(TabEntry.TabEntryBuilder builder) {
        initMap.put(builder.getX() * 20 + builder.getY(), builder);
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

    public static TabModifierBuilder builder() {
        return new TabModifierBuilder();
    }

}
