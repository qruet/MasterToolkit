package dev.qruet.toolkit.command;

import dev.qruet.toolkit.ToolKit;
import dev.qruet.toolkit.lang.Lang;
import dev.qruet.toolkit.lang.T;
import dev.qruet.toolkit.utility.Reflections;
import dev.qruet.toolkit.utility.Try;
import joptsimple.internal.Strings;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Qruet
 * <p>
 * Responsible for routing incoming command requests to the
 * appropriate executor instances
 */
public class CommandManager {

    private static final LinkedHashMap<String, CommandWrapper> MAP_BUFFER = new LinkedHashMap<>();

    private static String src_path;
    private static String cmd;

    /**
     * @param src     Class lookup package source
     * @param command Main/Default alias
     */
    public static void init(String src, String command) {
        CommandManager.src_path = src;
        CommandManager.cmd = command;

        List<String> packages = new ArrayList<>() {{
            add(src_path);
        }};
        packages.addAll(Reflections.getSubPackagePaths(src_path));

        packages.forEach(p -> {
            Reflections.findAllClassesInPackage(p, Command.class, false).forEach(c -> {
                Constructor<?> result = Arrays.stream(c.getDeclaredConstructors()).filter(con -> con.getParameterCount() == 0).findAny().orElse(null);
                result.setAccessible(true);
                Command cmd = Try.Instantiate(() -> (Command) result.newInstance());
                MAP_BUFFER.put(cmd.getAlias(), new CommandWrapper(cmd));
            });
        });
    }

    private static class CommandWrapper extends BukkitCommand {

        final Command command;

        private CommandWrapper(Command command) {
            super(command.getAlias());
            this.command = command;
        }

        @Override
        public boolean execute(CommandSender sender, String alias, String[] args) {
            try {
                Command.Response response = command.execute(sender, args);
                switch(response) {
                    case SYNTAX -> {
                        sender.sendMessage(Lang.Cmd.SYNTAX_ERROR.toString(
                                "/" + alias + " " + Strings.join(args, " "),
                                "/" + alias + " help"));
                        break;
                    }
                    case UNKNOWN -> {
                        sender.sendMessage(Lang.Cmd.UNKNOWN_ERROR.toString());
                        break;
                    }
                }
            } catch (Exception e) {
                ToolKit.getLogger().warning("An unknown error occurred while " + sender.getName() + " " +
                        "tried executing " +
                        "/" + alias + " " + Arrays.asList(args));
                e.printStackTrace();
                sender.sendMessage(Lang.Cmd.UNKNOWN_ERROR.toString());
            }
            return true;
        }
    }

    private static class CommandHandler implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
            return false;
        }
    }

}
