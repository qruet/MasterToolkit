package dev.qruet.toolkit.io.api;

import dev.qruet.toolkit.io.IOProfile;
import dev.qruet.toolkit.io.profile.IOType;

import java.io.File;

/**
 * @author Qruet
 * @version 0.0.1-ALPHA
 * <p>
 * Responsible for building IO profiles
 */
public class IOProfileBuilder {

    public static IOProfileBuilder construct(IOType type) {
        return new IOProfileBuilder(type);
    }

    private final IOType type;

    private String dir;
    private String extension;

    private IOProfileBuilder(IOType type) {
        this.type = type;
    }

    public IOProfileBuilder setDirectoryPath(File file) {
        this.dir = file.getAbsolutePath();
        return this;
    }

    public IOProfileBuilder setDirectoryPath(String path) {
        this.dir = path;
        return this;
    }

    public IOProfileBuilder setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public String getDirectoryPath() {
        return dir;
    }

    public String getExtension() {
        return extension;
    }

    public IOProfile build() {
        if(extension == null) {
            throw new UnsupportedOperationException("Can't build IOProfile without a defined file extension type.");
        }
        return type.buildProfile(dir, extension);
    }

}
