package dev.qruet.toolkit;

import dev.qruet.toolkit.tasky.Tasky;
import dev.qruet.toolkit.utility.reflection.Reflections;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ToolKit {

    private static JavaPlugin plugin;
    private static Logger logger;

    public final CommandManagerUtility COMMAND_MANAGER_UTILITY = new CommandManagerUtility("dev.qruet.toolkit.command.CommandManager");

    public static <T extends JavaPlugin> ToolKit load(T plugin) {
        if (ToolKit.plugin != null)
            throw new UnsupportedOperationException("ToolKit cannot be initialized more than once in a single instance.");
        ToolKit.plugin = plugin;
        Tasky.setPlugin(plugin);
        return new ToolKit();
    }

    public void unload() {
        ToolKit.plugin = null;
        ToolKit.logger = null;

        if (COMMAND_MANAGER_UTILITY.isAttached())
            COMMAND_MANAGER_UTILITY.detach();
    }

    /**
     * Hidden Constructor
     */
    private ToolKit() {
    }

    public static <T extends JavaPlugin> T getPlugin() {
        return (T) plugin;
    }

    public void setLogger(Logger logger) {
        ToolKit.logger = logger;
    }

    public static Logger getLogger() {
        return logger == null ? plugin.getLogger() : logger;
    }

    private abstract class UtilityRef {
        protected final String path;
        protected boolean attached = false;

        protected UtilityRef(final String path) {
            this.path = path;
        }

        protected ToolKit attach(Object... values) {
            if (attached)
                throw new UnsupportedOperationException("Already attached.");
            Reflections.invokeStaticMethod(path, "enable", values);
            attached = true;
            return ToolKit.this;
        }


        protected void detach(Object... values) {
            if (!attached)
                throw new UnsupportedOperationException(getClass().getName() + " needs to be attached first before detaching.");
            Reflections.invokeStaticMethod(path, "unload", values);
            attached = false;
        }

        public boolean isAttached() {
            return attached;
        }
    }

    public class CommandManagerUtility extends UtilityRef {

        protected CommandManagerUtility(String path) {
            super(path);
        }

        /**
         * Calls on {@link dev.qruet.toolkit.command.CommandManager#enable(String)}
         *
         * @param src Source for commands
         */
        public ToolKit attach(String src) {
            return super.attach(src);
        }

        /**
         * Calls on {@link dev.qruet.toolkit.command.CommandManager#unload()}
         */
        private void detach() {
            super.detach();
        }

    }

}
