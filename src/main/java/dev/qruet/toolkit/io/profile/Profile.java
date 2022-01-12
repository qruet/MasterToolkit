package dev.qruet.toolkit.io.profile;

import dev.qruet.toolkit.ToolKit;
import dev.qruet.toolkit.io.IOProfile;

import java.io.File;
import java.nio.file.Paths;

public abstract class Profile implements IOProfile {

    protected final File dir;
    protected final String ext;

    protected Verbosity verbose = Verbosity.LOW;

    protected Profile(String path, String extension) {
        this.dir = new File(path.isBlank() ? Paths.get("").toAbsolutePath().toString() : path);
        this.ext = extension.replaceAll("\\.", "");
    }

    @Override
    public void setVerbose(Verbosity verbose) {
        this.verbose = verbose;
    }

    protected void tell(Verbosity priority, String message) {
        if(!canTell(priority))
            return;

        switch (priority) {
            case HIGH -> {
                ToolKit.getLogger().severe(message);
                break;
            }
            case MEDIUM -> {
                ToolKit.getLogger().warning(message);
                break;
            }
            case LOW -> {
                ToolKit.getLogger().info(message);
                break;
            }
        }
    }

    private final boolean canTell(Verbosity priority) {
        if (priority == Verbosity.SILENT)
            return false;

        if (priority == Verbosity.HIGH)
            return true;

        return verbose.getLevel() - Math.ceil(((float) Verbosity.values().length / (float) priority.getLevel())) >= 0;
    }

}
