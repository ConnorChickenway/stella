package xyz.connorchickenway.stella.tab.modifier;

import xyz.connorchickenway.stella.tab.entry.TabEntry;

import java.util.List;

@FunctionalInterface
public interface TabInit {

    List<TabEntry.TabEntryBuilder> init();

}
