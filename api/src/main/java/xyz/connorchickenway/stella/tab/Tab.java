package xyz.connorchickenway.stella.tab;

import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.modifier.TabUpdate;

public interface Tab {

    int WIDTH = 4, HEIGHT = 20;
    int LEFT = 0, MIDDLE = 1, MIDDLE_RIGHT = 2, RIGHT = 3;

    void init(TabModifier tabModifier);
    void update(TabUpdate tabUpdate);
    boolean isCreating();
    TabEntry buildEntry(int x, int y, TabModifier tabModifier);
    TabEntry getEntryByPosition(int x, int y);

}
