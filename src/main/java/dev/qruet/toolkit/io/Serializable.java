package dev.qruet.toolkit.io;

import dev.qruet.toolkit.ToolKit;

import java.io.File;

public interface Serializable {

    byte[] toByteStream();

    default File getIOPath() {
        return ToolKit.getPlugin().getDataFolder();
    }

}
