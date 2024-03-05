package xyz.connorchickenway.stella.tab.modifier;

import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.entry.TabEntry;

import java.util.List;

@FunctionalInterface
public interface TabUpdate {

    List<TabEntry> update(Player player);

}
