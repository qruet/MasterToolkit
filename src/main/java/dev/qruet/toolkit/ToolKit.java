package dev.qruet.toolkit;

import dev.qruet.toolkit.tasky.Tasky;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ToolKit {

    private static JavaPlugin plugin;
    private static Logger logger;

    public static <T extends JavaPlugin> void init(T plugin) {
        ToolKit.plugin = plugin;
        Tasky.setPlugin(plugin);
    }

    public static <T extends JavaPlugin> T getPlugin() {
        return (T) plugin;
    }

    public static void setLogger(Logger logger) {
        ToolKit.logger = logger;
    }

    public static Logger getLogger() {
        return logger == null ? plugin.getLogger() : logger;
    }

}
