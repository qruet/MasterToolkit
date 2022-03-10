package dev.qruet.toolkit.io;

import java.io.File;

public interface IOProfile {

    enum Extensions {
        YAML("yaml"), DATA("dat");

        final String ext;

        Extensions(String ext) {
            this.ext = ext;
        }

        @Override
        public String toString() {
            return ext;
        }
    }

    enum Verbosity {
        SILENT(0), LOW(1), MEDIUM(2), HIGH(3);

        final int level;

        Verbosity(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    void setVerbose(Verbosity verbose);

}
