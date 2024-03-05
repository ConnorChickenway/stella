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

package xyz.connorchickenway.stella;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.connorchickenway.stella.legacy.tab.LegacyTab;
import xyz.connorchickenway.stella.tab.SimpleTab;
import xyz.connorchickenway.stella.tab.Tab;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.options.Options;
import xyz.connorchickenway.stella.tab.task.TabHandler;
import xyz.connorchickenway.stella.tab.task.TabTask;
import xyz.connorchickenway.stella.util.ServerVersion;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Stella {

    private final JavaPlugin plugin;
    private final Map<UUID, Tab> tabs = new ConcurrentHashMap<>();
    private Options options;
    private final TabModifier tabModifier;
    private TabTask tabTask;

    private Stella(JavaPlugin plugin, TabModifier tabModifier) {
        this.plugin = plugin;
        this.tabModifier = tabModifier;
    }

    private Stella(JavaPlugin plugin, TabModifier tabModifier, Options options) {
        this(plugin, tabModifier);
        this.options = options;
        tabTask = new TabHandler(this, tabModifier);
        if (options.isAsync()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, tabTask, 0L, options.delay());
        }
        else {
            Bukkit.getScheduler().runTaskTimer(plugin, tabTask, 0L, options.delay());
        }
        if (options.listener()) {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    Player player = event.getPlayer();
                    Tab tab = add(player);
                    Runnable runnable = () -> tab.init(tabModifier);
                    if (options.isAsync()) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, 1L);
                        return;
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, runnable, 1L);
                }

                @EventHandler
                public void onQuit(PlayerQuitEvent event) {
                    remove(event.getPlayer());
                }

            }, plugin);
        }
    }

    public Tab add(Player player) {
        Tab tab = ServerVersion.isLegacy()? new LegacyTab(player) : new SimpleTab(player);
        tabs.put(player.getUniqueId(), tab);
        return tab;
    }

    public void remove(Player player) {
        tabs.remove(player.getUniqueId());
    }

    public Tab getTab(Player player) {
        return tabs.get(player.getUniqueId());
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<UUID, Tab> getTabs() {
        return tabs;
    }

    public TabModifier getTabModifier() {
        return tabModifier;
    }

    public Options getOptions() {
        return options;
    }

    public TabTask getTabTask() {
        return tabTask;
    }

    public static class StellaBuilder {

        private JavaPlugin plugin;
        private TabModifier tabModifier;
        private Options options;

        public StellaBuilder plugin(JavaPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public StellaBuilder tabModifier(TabModifier tabModifier) {
            this.tabModifier = tabModifier;
            return this;
        }

        public StellaBuilder options(Options options) {
            this.options = options;
            return this;
        }

        public Stella build() {
            if (options != null) {
                return new Stella(plugin, tabModifier, options);
            }
            return new Stella(plugin, tabModifier);
        }
    }

    public static StellaBuilder builder() {
        return new StellaBuilder();
    }

}

