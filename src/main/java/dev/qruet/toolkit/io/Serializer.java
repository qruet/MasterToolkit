package dev.qruet.toolkit.io;

public interface Serializer {

    boolean serialize(String fileName, Serializable serializable, boolean append);

}
