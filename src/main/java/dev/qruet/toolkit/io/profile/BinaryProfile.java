package dev.qruet.toolkit.io.profile;

import dev.qruet.toolkit.io.Deserializer;
import dev.qruet.toolkit.io.IOProfile;
import dev.qruet.toolkit.io.Serializable;
import dev.qruet.toolkit.io.Serializer;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BinaryProfile extends Profile implements Serializer, Deserializer {

    protected BinaryProfile(String file_path, String default_extension) {
        super(file_path, default_extension);
    }

    public boolean serialize(String fileName, Set<Map.Entry<String, String>> data) {

        if (fileName.contains(".")) {
            throw new UnsupportedOperationException("File name cannot already contain a file extension.");
        }

        String ffileName = fileName + "." + ext;

        File file;
        File[] files = dir.listFiles((f) -> {
            return f.getName().equals(ffileName);
        });
        if (files == null || files.length == 0) {
            file = new File(dir, ffileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else
            file = files[0];

        try {
            OutputStream out = new FileOutputStream(file, false);
            for (Map.Entry<String, String> entry : data) {
                out.write(entry.getKey().getBytes());
                out.write(255);
                out.write(entry.getValue().getBytes());
                out.write(255);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean serialize(Serializable serializable) {
        try {
            OutputStream out = new FileOutputStream(serializable.getIOPath(), false);
            out.write(serializable.toByteStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Set<Map.Entry<String, String>> deserialize(String fileName) {
        if (fileName.contains(".")) {
            throw new UnsupportedOperationException("File name cannot already contain a file extension.");
        }

        String ffileName = fileName + "." + ext;

        File file = new File(dir, ffileName);
        if (!file.exists()) {
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

                if (i == 255 && !f1) {
                    f1 = true;
                } else if (i == 255 && f1) {
                    out.put(key.toString(), val.toString());

                    key = new StringBuilder();
                    val = new StringBuilder();
                } else if (i != 255) {
                    if (!f1)
                        key.append((char) i);
                    else
                        val.append((char) i);
                }
            } while (i != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.entrySet();
    }

}
