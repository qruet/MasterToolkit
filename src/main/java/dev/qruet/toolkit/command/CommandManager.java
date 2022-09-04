package dev.qruet.toolkit.command;

import dev.qruet.toolkit.ToolKit;
import dev.qruet.toolkit.lang.Lang;
import dev.qruet.toolkit.utility.Try;
import dev.qruet.toolkit.utility.reflection.Reflections;
import joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.help.SimpleHelpMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
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

    /**
     * @param src Class lookup package source
     */
    public static void enable(String src) {
        CommandManager.src_path = src;

        List<String> packages = new ArrayList<>() {{
            add(src_path);
        }};

        packages.addAll(Reflections.getSubPackagePaths(src_path));

        SimpleCommandMap map = Reflections.getValue(SimplePluginManager.class, Bukkit.getPluginManager(), "commandMap");
        Server server = ToolKit.getPlugin().getServer();
        SimpleHelpMap helpMap = Reflections.getValue(CraftServer.class, server, "helpMap");

        packages.forEach(p -> {
            Reflections.findAllClassesInPackage(p, Command.class, false).forEach(c -> {
                Constructor<?> result = Arrays.stream(c.getDeclaredConstructors()).filter(con -> con.getParameterCount() == 0).findAny().orElse(null);
                result.setAccessible(true);
                Command cmd = Try.Instantiate(() -> (Command) result.newInstance());

                CommandWrapper wrapper = new CommandWrapper(cmd);
                MAP_BUFFER.put(cmd.getKey(), wrapper);

                map.register(wrapper.getName(), wrapper);
            });
        });


        map.setFallbackCommands();
        map.registerServerAliases();
        Reflections.invokeMethod("initializeCommands", helpMap);
        ((CraftServer) server).syncCommands();
    }

    public static void unload() {
        SimpleCommandMap map = Reflections.getValue((SimplePluginManager) Bukkit.getPluginManager(), "commandMap");

        Map<String, org.bukkit.command.Command> knownCommands = Reflections.getValue(SimpleCommandMap.class, map, "knownCommands");
        MAP_BUFFER.forEach((k, v) -> {
            v.getAliases().forEach(knownCommands::remove);
            knownCommands.remove(k);
            v.unregister(map);
        });
    }

    private static class CommandWrapper extends org.bukkit.command.Command implements PluginIdentifiableCommand {

        final Command command;

        private CommandWrapper(Command command) {
            super(command.getKey());
            this.command = command;
            setAliases(command.getAliases());
        }

        @Override
        public boolean execute(CommandSender sender, String alias, String[] args) {
            try {
                Command.Response response = command.execute(sender, args);
                switch (response) {
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
            } catch (Error e) {
                ToolKit.getLogger().warning("An unknown error occurred while " + sender.getName() + " " +
                        "tried executing " +
                        "/" + alias + " " + Arrays.asList(args));
                e.printStackTrace();
                sender.sendMessage(Lang.Cmd.UNKNOWN_ERROR.toString());
            }
            return true;
        }

        @Override
        public Plugin getPlugin() {
            return ToolKit.getPlugin();
        }
    }

}
