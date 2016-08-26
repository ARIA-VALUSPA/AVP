package eu.aria.core;

/**
 * Created by adg on 27/11/2015.
 *
 */
public class Config {

    public static final Config DEFAULT = new Builder().withSSIWindows().build();

    private boolean showSSIWindows = false;
    private boolean showAgentWindows = false;

    private Config() {
    }

    public boolean showSSIWindows() {
        return showSSIWindows;
    }

    public boolean showAgentWindows() {
        return showAgentWindows;
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

        public Builder withAgentWindows() {
            checkState();
            config.showAgentWindows = true;
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
