package eu.aria.core;

/**
 * Created by adg on 27/11/2015.
 */
public class Config {

    public static final Config DEFAULT = new Builder().withSSIWindows().build();

    private boolean showSSIWindows = false;

    private Config() {
    }

    public boolean showSSIWindows() {
        return showSSIWindows;
    }

    public static class Builder {

        private boolean done = false;
        private Config config = new Config();

        public void reset() {
            done = false;
            config = new Config();
        }

        public Builder withSSIWindows() {
            checkState();
            config.showSSIWindows = true;
            return this;
        }

        public Config build() {
            checkState();
            done = true;
            return config;
        }

        private void checkState() {
            if (done) {
                throw new IllegalStateException("Used builder");
            }
        }
    }
}
