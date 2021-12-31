package dev.qruet.toolkit.io;

import java.util.Map;
import java.util.Set;

public class IOFormatter {

    public interface Profile {
        boolean serialize(String fileName, Set<Map.Entry<String, String>> data);
        default Set<Map.Entry<String, String>> deserialize(String fileName) {
            return deserialize(fileName, false);
        }
        Set<Map.Entry<String, String>> deserialize(String fileName, boolean verbose);
    }

    private static class BinaryProfile implements Profile {



}
