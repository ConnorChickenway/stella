package xyz.connorchickenway.stella.tab.modifier;

import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TabModifier {

    private Map<Position, TabEntry.TabEntryBuilder> initMap;
    private TabUpdate tabUpdate;

    private TabModifier(TabInit tabInit) {
        if (tabInit != null) {
            this.initMap = new ConcurrentHashMap<>();
            tabInit.init()
                    .stream()
                    .filter(entry -> entry.getX() != null && entry.getY() != null)
                    .filter(entry -> entry.getX() > 0 && entry.getX() < PlayerTab.WIDTH)
                    .filter(entry -> entry.getY() > 0 && entry.getY() < PlayerTab.HEIGHT)
                    .forEach(this::add);
        }

    }

    public TabEntry.TabEntryBuilder getTabEntryInitByPosition(int x, int y) {
        return initMap.get(Position.of(x, y));
    }

    public TabUpdate getTabUpdate() {
        return tabUpdate;
    }

    private void add(TabEntry.TabEntryBuilder tabEntryBuilder) {
        initMap.put(Position
                .of(tabEntryBuilder.getX(),
                        tabEntryBuilder.getY()),
                tabEntryBuilder);
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

    private static class Position {
        private final int x, y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Position) {
                if (this == o) return true;
                Position tmpPosition = (Position) o;
                return this.x == tmpPosition.x && this.y == tmpPosition.y;
            }
            return false;
        }

        public static Position of(int x, int y) {
            return new Position(x, y);
        }

    }

}
