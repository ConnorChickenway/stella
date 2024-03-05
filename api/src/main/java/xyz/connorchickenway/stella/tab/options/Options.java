package xyz.connorchickenway.stella.tab.options;

public class Options {

    private boolean async;
    private long delay;
    private boolean listener;

    private Options() {}

    public boolean isAsync() {
        return this.async;
    }

    public long delay() {
        return this.delay;
    }

    public boolean listener() {
        return listener;
    }

    public static class OptionsBuilder {
        private boolean async;
        private long delay;
        private boolean listener;

        public OptionsBuilder async(boolean async) {
            this.async = async;
            return this;
        }

        public OptionsBuilder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public OptionsBuilder addListener(boolean listener) {
            this.listener = listener;
            return this;
        }

        public Options build() {
            Options options = new Options();
            options.async = this.async;
            options.delay = this.delay;
            options.listener = this.listener;
            return options;
        }
    }

    public static OptionsBuilder builder() {
        return new OptionsBuilder();
    }

}
