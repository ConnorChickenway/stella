package xyz.connorchickenway.stella.tab;

import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;

public interface Tab {

    int WIDTH = 4, HEIGHT = 20;

    void init(TabModifier tabModifier);
    boolean isCreating();
    TabEntry buildEntry(int x, int y, TabModifier tabModifier);
    TabEntry getEntryByPosition(int x, int y);

}
