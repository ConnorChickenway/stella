package xyz.connorchickenway.stella.tab.options;

public class Options {

    private boolean async;
    private long delay;
    private boolean addListener;

    private Options() {}

    public boolean isAsync() {
        return this.async;
    }

    public long delay() {
        return this.delay;
    }

    public boolean addListener() {
        return addListener;
    }

    public static class OptionsBuilder {
        private boolean async;
        private long delay;
        private boolean addListener;

        public OptionsBuilder async(boolean async) {
            this.async = async;
            return this;
        }

        public OptionsBuilder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public OptionsBuilder addListener(boolean addListener) {
            this.addListener = addListener;
            return this;
        }

        public Options build() {
            Options options = new Options();
            options.async = this.async;
            options.delay = this.delay;
            options.addListener = this.addListener;
            return options;
        }

    }

}
