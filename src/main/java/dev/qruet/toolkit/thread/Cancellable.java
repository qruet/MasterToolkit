package dev.qruet.toolkit.thread;

import dev.qruet.toolkit.utility.Try;

public abstract class Cancellable extends Thread {

    private boolean cancelled = false;

    private long delay;

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void run() {
        while (true) {
            if (this.cancelled)
                return;
            this.onRun();
            Try.Catch(() -> Thread.sleep(delay), InterruptedException.class);
        }
    }

    public final Cancellable delay(long delay) {
        this.delay = delay;
        return this;
    }

    protected abstract void onRun();

    public final void cancel() {
        if (this.isCancelled()) // already cancelled?
            return;
        this.cancelled = true;
        this.onCancel();
    }

    protected abstract void onCancel();

}
