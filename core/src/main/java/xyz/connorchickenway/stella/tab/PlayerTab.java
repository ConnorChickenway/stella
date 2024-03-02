package xyz.connorchickenway.stella.tab;

import org.bukkit.entity.Player;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;

import java.util.concurrent.atomic.AtomicBoolean;

import static xyz.connorchickenway.stella.tab.entry.TabEntry.TabEntryBuilder;

public abstract class PlayerTab implements Tab {

    protected final Player player;
    protected final TabEntry[][] entries;
    protected final AtomicBoolean creating;

    public PlayerTab(Player player, TabEntry[][] entries) {
        this.player = player;
        this.entries = entries;
        this.creating = new AtomicBoolean(true);
    }

    public PlayerTab(Player player) {
        this(player, new TabEntry[WIDTH][HEIGHT]);
    }

    @Override
    public boolean isCreating() {
        return creating.get();
    }

    @Override
    public TabEntry buildEntry(int x, int y, TabModifier tabModifier) {
        TabEntryBuilder entry = tabModifier.getTabEntryInitByPosition(x, y);
        if (entry == null)
            entry = TabEntryBuilder.createBlankEntry(x, y);
        return entries[x][y] = entry.build(this);
    }

    @Override
    public TabEntry getEntryByPosition(int x, int y) {
        return entries[x][y];
    }

    public Player getPlayer() {
        return player;
    }
}
