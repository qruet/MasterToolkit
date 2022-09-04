package dev.qruet.toolkit.io.profile;

import dev.qruet.toolkit.io.IOProfile;
import dev.qruet.toolkit.utility.reflection.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileLibrary {

    private static Map<String, Constructor<IOProfile>> MAP = new HashMap<>();

    static {
        // populate
        try {
            for (Class<IOProfile> clazz : Reflections.findAllClassesInPackage(ProfileLibrary.class.getPackageName(), IOProfile.class, false)) {
                MAP.put(clazz.getSimpleName().replaceAll("Profile", "").toUpperCase(Locale.ROOT), clazz.getDeclaredConstructor(String.class, String.class));
            }
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static IOProfile buildProfile(String profileName, Object... params) {
        Constructor<IOProfile> construct = MAP.get(profileName.toUpperCase(Locale.ROOT));
        if (construct == null)
            return null;

        construct.setAccessible(true);
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
