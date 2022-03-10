package dev.qruet.toolkit.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiFunction;

public abstract class Command {

    protected final String alias;
    private BiFunction<CommandSender, String[], Response> func1;
    private BiFunction<Player, String[], Response> func2;

    protected Command(String alias) {
        this.alias = alias;
        this.func1 = this::run;

        for (Method m : this.getClass().getDeclaredMethods()) {
            Class<?>[] parameters = m.getParameterTypes();
            if (parameters.length != 2 || parameters[0] != Player.class || parameters[1] != String[].class)
                continue;

            m.setAccessible(true);
            this.func2 = (player, strings) -> {
                Response response = Response.UNKNOWN;
                try {
                    response = (Response) m.invoke(this, player, strings);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return response;
            };

            for (Annotation an : m.getDeclaredAnnotations()) {
                // TODO Build function profile based off of optional annotations:
                //   * Permission Annotation
                //   * Arguments Annotation
            }
        }
    }


    public final Response execute(CommandSender sender, String[] args) {
        if (func2 != null && sender instanceof Player player) {
            return func2.apply(player, args);
        } else if (func1 != null) {
            return func1.apply(sender, args);
        }

        return Response.UNKNOWN;
    }

    /**
     * Default executor function for handling commands.
     * <p>
     * An optional function, Command#run(Player sender, String[] args)
     * can be added. This function will then only be called when console
     * executes a command and the alternative optional Command#run function
     * will only be executed when run by a player.
     *
     * @param sender CommandSender or Player or Console only (see description)
     * @param args   Command arguments
     * @return Response type, use {@link Response.SUCCESS} by default for successful execution
     */
    protected abstract Response run(CommandSender sender, String[] args);

    public String getAlias() {
        return alias;
    }

    public enum Response {
        UNKNOWN, SYNTAX, PERMISSION, ARGUMENTS, CONSOLE_FAIL, PLAYER_FAIL, SUCCESS
    }

}
