package dev.qruet.toolkit;

public abstract class InitializableTool {

    private boolean initialized = false;

    public final void init() {
        if (initialized)
            throw new UnsupportedOperationException(getClass().getName() + " has already been initialized.");
        initialized = true;
    }

    protected abstract void onInit();

}
