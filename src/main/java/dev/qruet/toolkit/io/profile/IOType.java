package dev.qruet.toolkit.io.profile;

import dev.qruet.toolkit.io.IOProfile;

public enum IOType {

    BINARY;

    public IOProfile buildProfile(Object... params) {
        return ProfileLibrary.buildProfile(toString());
    }

}
