package xyz.connorchickenway.stella.tab.task;

import xyz.connorchickenway.stella.tab.modifier.TabModifier;

public class TabHandler implements TabTask {

    private final TabModifier tabModifier;

    public TabHandler(TabModifier tabModifier) {
        this.tabModifier = tabModifier;
    }

    @Override
    public void run() {

    }
}
