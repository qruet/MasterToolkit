package dev.qruet.toolkit.io.profile;

import dev.qruet.toolkit.ToolKit;
import dev.qruet.toolkit.io.IOProfile;

import java.io.File;
import java.io.IOException;
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
    public File buildFile(String file, boolean replace) {
        File fileObj = new File(dir, file + "." + ext);
        if (replace && fileObj.exists())
            fileObj.delete();

        if (!fileObj.exists()) {
            try {
                File parent = fileObj.getParentFile(); // get parent directory
                if (parent != null) // check if parent directory is specified/exists
                    parent.mkdirs(); // build directories

                fileObj.createNewFile(); // create file
            } catch (IOException e) {
                System.err.println("Failed to create log file, \"" + dir.getPath() + "/" + file + "." + ext + "\".");
                return null;
            }
        }

        return fileObj;
    }

    @Override
    public void setVerbose(Verbosity verbose) {
        this.verbose = verbose;
    }

    protected void tell(Verbosity priority, String message) {
        if (!canTell(priority))
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
