/*
 *     Stella - A tablist API
 *     Copyright (C) 2024  ConnorChickenway
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
