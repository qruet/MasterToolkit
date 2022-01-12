package dev.qruet.toolkit.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public abstract class Command {

    protected final String alias;
    private BiFunction<CommandSender, String[], Response> func1;
    private BiFunction<Player, String[], Response> func2;

    protected Command(String alias) {
        this.alias = alias;
    }

    public final Response execute(CommandSender sender, String[] args) {
        if (func2 != null && sender instanceof Player player) {
            return func2.apply(player, args);
        } else if (func1 != null) {
            return func1.apply(sender, args);
        }

        return Response.UNKNOWN;
    }

    public String getAlias() {
        return alias;
    }

    public enum Response {
        UNKNOWN, SYNTAX, PERMISSION, ARGUMENTS, CONSOLE_FAIL, PLAYER_FAIL, SUCCESS
    }

}
