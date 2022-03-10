package dev.qruet.toolkit.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    String[] value();

}
