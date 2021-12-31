package dev.qruet.toolkit.io.profile;

import dev.qruet.toolkit.io.IOProfile;
import dev.qruet.toolkit.utility.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileLibrary {

    private static Map<String, Constructor<IOProfile>> MAP = new HashMap<>();

    static {
        // populate
        Reflections.findAllClassesInPackage(ProfileLibrary.class.getPackageName()).stream().filter(c ->
                Profile.class.isAssignableFrom(c)).forEach(c -> {
            Constructor<IOProfile> construct = c.getDeclaredConstructors()[0];
            construct.setAccessible(true);
            MAP.put(c.getSimpleName().replaceAll("Library", "").toUpperCase(Locale.ROOT), construct);
        });
    }

    public static IOProfile buildProfile(String profileName, Object... params) {
        Constructor<IOProfile> construct = MAP.get(profileName.toUpperCase(Locale.ROOT));
        if (construct == null)
            return null;

        IOProfile profile = null;
        try {
            profile = construct.newInstance(params);
        } catch (InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return profile;
    }


}
