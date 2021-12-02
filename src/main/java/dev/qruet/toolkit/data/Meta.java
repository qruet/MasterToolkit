package dev.qruet.toolkit.data;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Meta {

    private static Logger logger;

    public enum FileType {
        BINARY, YAML
    }

    public static void enableVerbose(Logger logger) {
        Meta.logger = logger;
    }

    public static Profile buildProfile(String root_path, FileType type, String extension) {
        switch(type) {
            default:
                return new BinaryProfile(root_path, extension);
        }
    }

    public interface Profile {
        boolean serialize(String fileName, Set<Map.Entry<String, String>> data);
        default Set<Map.Entry<String, String>> deserialize(String fileName) {
            return deserialize(fileName, false);
        }
        Set<Map.Entry<String, String>> deserialize(String fileName, boolean verbose);
    }

    private static class BinaryProfile implements Profile {

        private final File dir;
        private final String ext;

        private BinaryProfile(String file_path, String default_extension) {
            this.dir = new File(file_path.isBlank() ? Paths.get("").toAbsolutePath().toString() : file_path);
            this.ext = default_extension.replaceAll("\\.", "");

            if(!dir.exists())
                dir.mkdirs();
        }

        public boolean serialize(String fileName, Set<Map.Entry<String, String>> data) {
            if(fileName.contains(".")) {
                throw new UnsupportedOperationException("File name cannot already contain a file extension.");
            }

            String ffileName = fileName + "." + ext;

            File file;
            File[] files = dir.listFiles((f) -> {
                return f.getName().equals(ffileName);
            });
            if(files == null || files.length == 0) {
                file = new File(dir, ffileName);
                try {
                    file.createNewFile();
                } catch(IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else
                file = files[0];

            try {
                OutputStream out = new FileOutputStream(file, false);
                for(Map.Entry<String, String> entry : data) {
                    out.write(entry.getKey().getBytes());
                    out.write(255);
                    out.write(entry.getValue().getBytes());
                    out.write(255);
                }
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public Set<Map.Entry<String, String>> deserialize(String fileName, boolean verbose) {
            if(fileName.contains(".")) {
                throw new UnsupportedOperationException("File name cannot already contain a file extension.");
            }

            String ffileName = fileName + "." + ext;

            File file = new File(dir, ffileName);
            if(!file.exists()) {
                if(logger != null && verbose)
                    logger.warning("Failed to retrieve file " + file.getPath());
                return Collections.emptySet();
            }

            Map<String, String> out = new HashMap<>();
            try {
                InputStream in = new FileInputStream(file);

                boolean f1 = false;
                StringBuilder key = new StringBuilder();
                StringBuilder val = new StringBuilder();
                int i;
                do {
                    i = in.read();

                    if(i == 255 && !f1) {
                        f1 = true;
                    } else if(i == 255 && f1) {
                        out.put(key.toString(), val.toString());

                        key = new StringBuilder();
                        val = new StringBuilder();
                    } else if(i != 255) {
                        if(!f1)
                            key.append((char) i);
                        else
                            val.append((char) i);
                    }
                } while(i != -1);
            } catch(IOException e) {
                e.printStackTrace();
            }

            return out.entrySet();
        }

    }
}
