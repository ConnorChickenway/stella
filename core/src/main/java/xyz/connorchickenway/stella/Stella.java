package xyz.connorchickenway.stella;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.connorchickenway.stella.tab.Tab;
import xyz.connorchickenway.stella.tab.options.Options;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Stella {

    private final JavaPlugin plugin;
    private final Map<UUID, Tab> tabs = new ConcurrentHashMap<>();
    private Options options;

    public Stella(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Stella(JavaPlugin plugin, Options options) {
        this(plugin);
        this.options = options;

    }

    public void add(Player player) {
        //tabs.put(player.getUniqueId())

    }

    public Tab getTab(Player player) {
        return tabs.get(player.getUniqueId());
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Options getOptions() {
        return options;
    }

}
