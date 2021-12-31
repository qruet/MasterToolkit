package dev.qruet.toolkit.tasky;

import dev.qruet.toolkit.ToolKit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.lang.ref.WeakReference;

/**
 * Tasky.java
 * <p>
 * Requires Java 8, uses lambda expressions.
 *
 * @author PLARSON (Wowserman)
 * https://www.spigotmc.org/members/wowserman.124342/
 * <p>
 * Created Jun 29, 2018
 */

/**
 * Modified for personal use
 * @author Qruet
 * @version 3.0.1
 */
public final class Tasky {

    public interface Response<T> {
        void onCompletion(T t);
    }

    public static Thread async(final Thread thread) {
        thread.start();
        return thread;
    }

    /// MARK

    public static BukkitRunnable async(final Consumer<BukkitRunnable> block) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                block.accept(this);
            }

        };
        runnable.runTaskAsynchronously(ToolKit.getPlugin());
        return runnable;
    }

    public static BukkitRunnable sync(final Consumer<BukkitRunnable> block) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                block.accept(this);
            }
        };
        runnable.runTask(ToolKit.getPlugin());
        return runnable;
    }

    public static BukkitRunnable async(final Consumer<BukkitRunnable> block, final long delay) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                block.accept(this);
            }
        };
        runnable.runTaskLaterAsynchronously(ToolKit.getPlugin(), delay);
        return runnable;
    }

    public static BukkitRunnable sync(final Consumer<BukkitRunnable> block, final long delay) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                block.accept(this);
            }
        };
        runnable.runTaskLater(ToolKit.getPlugin(), delay);
        return runnable;
    }

    public static BukkitRunnable sync(final Consumer<BukkitRunnable> block, final long delay, final long period) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                block.accept(this);
            }
        };
        runnable.runTaskTimer(ToolKit.getPlugin(), delay, period);
        return runnable;
    }

    public static BukkitRunnable async(final Consumer<BukkitRunnable> block, final long delay, final long period) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                block.accept(this);
            }
        };
        runnable.runTaskTimerAsynchronously(ToolKit.getPlugin(), delay, period);
        return runnable;
    }
}