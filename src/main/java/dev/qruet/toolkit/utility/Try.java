package dev.qruet.toolkit.utility;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * A class dedicated to cleaning up try, catch statements
 * @author Qruet
 */
public class Try {

    public static <T> T Catch(Supplier<T> supplier, Class<? extends Exception>... exceptions) {
        T t = null;
        try {
            t = supplier.get();
        } catch(Exception e) {
            e.printStackTrace();
            if(exceptions.length > 0) {
                for (Class<? extends Exception> exception : exceptions) {
                    if (exception == e.getClass())
                        return null;
                }
                throw new UnsupportedOperationException("Unexpected Exception Thrown, " + e);
            }
        }
        return t;
    }

    public static <T> T Catch(Supplier<T> supplier, Function<Exception, T> exceptionHandler, Class<? extends Exception>... exceptions) {
        T t = null;
        try {
            t = supplier.get();
        } catch(Exception e) {
            if(exceptions.length > 0) {
                for (Class<? extends Exception> exception : exceptions) {
                    if (exception == e.getClass())
                        return exceptionHandler.apply(e);
                }
                throw new UnsupportedOperationException("Unexpected Exception Thrown, " + e);
            }
        }
        return t;
    }

    public static <T> T Instantiate(Supplier<T> supplier) {
        return Catch(supplier, InstantiationException.class, IllegalAccessException.class, IllegalArgumentException.class, InvocationTargetException.class);
    }

    public static <T> T Instantiate(Supplier<T> supplier, Function<Exception, T> exceptionHandler) {
        return Catch(supplier, exceptionHandler, InstantiationException.class, IllegalAccessException.class, IllegalArgumentException.class, InvocationTargetException.class);
    }

    public interface Supplier<T> {

        T get() throws Exception;

    }
}
