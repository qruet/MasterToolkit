package dev.qruet.toolkit.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Arguments {

    /**
     * Use standard String format codes for variable arguments
     * e.g. %s (String/Names), %i (Integers) ..
     */
    String[] value();

}
