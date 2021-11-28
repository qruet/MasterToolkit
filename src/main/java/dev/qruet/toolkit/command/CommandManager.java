package dev.qruet.toolkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager {

    private class CommandHandler implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
            return false;
        }
    }

    private class CommandBuffer {

    }

}
