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

package xyz.connorchickenway.stella.tab.task;

import xyz.connorchickenway.stella.Stella;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;

public class TabHandler implements TabTask {

    private final Stella stella;
    private final TabModifier tabModifier;

    public TabHandler(Stella stella, TabModifier tabModifier) {
        this.stella = stella;
        this.tabModifier = tabModifier;
    }

    @Override
    public void run() {
        stella.getTabs().forEach((uuid, tab) -> tab.update(tabModifier.getTabUpdate()));
    }
}