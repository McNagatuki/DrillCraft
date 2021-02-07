package com.github.mcnagatuki.drillcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private boolean same(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0)
            return false;

        // start
        if (same(args[0], "start")) {
            DrillCraft.plugin.start();
            sender.sendMessage("Drill plugin is started.");
            return true;
        }

        // stop
        if (same(args[0], "stop")) {
            DrillCraft.plugin.stop();
            sender.sendMessage("Drill plugin is stopped.");
            return true;
        }

        // theta <Double>
        if (args.length == 2 && same(args[0], "theta")) {
            try {
                double theta = Double.parseDouble(args[1]);
                DrillCraft.plugin.config.theta = theta;
                sender.sendMessage(String.format("\"theta\" が %s に設定されました。", args[1]));
            } catch (Exception ignore) {
                return false;
            }
            return true;
        }

        // directed <Boolean>
        if (args.length == 2 && same(args[0], "directed")) {
            if (same(args[1], "true")) {
                DrillCraft.plugin.config.directed = true;
                sender.sendMessage(String.format("\"directed\" が %s に設定されました。", args[1]));
                return true;
            }

            if (same(args[1], "false")) {
                DrillCraft.plugin.config.directed = false;
                sender.sendMessage(String.format("\"directed\" が %s に設定されました。", args[1]));
                return true;
            }

            return false;
        }

        // droppable <Boolean>
        if (args.length == 2 && same(args[0], "droppable")) {
            if (same(args[1], "true")) {
                DrillCraft.plugin.config.droppable = true;
                sender.sendMessage(String.format("\"droppable\" が %s に設定されました。", args[1]));
                return true;
            }

            if (same(args[1], "false")) {
                DrillCraft.plugin.config.droppable = false;
                sender.sendMessage(String.format("\"droppable\" が %s に設定されました。", args[1]));
                return true;
            }

            return false;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();

        String[] cmds = {
                "start",
                "stop",
                "theta",
                "directed",
                "droppable",
        };

        if (args.length == 1) {
            for (String cmd : cmds) {
                if (cmd.startsWith(args[0])) {
                    result.add(cmd);
                }
            }
        }

        if (args.length == 2 && same(args[0], "theta")) {
            if (args[1].length() == 0) {
                String suggestion = String.valueOf(DrillCraft.plugin.config.theta);
                result.add(suggestion);
            }
        }

        if (args.length == 2 && same(args[0], "directed")) {
            if ("true".startsWith(args[1])) {
                result.add("true");
            }

            if ("false".startsWith(args[1])) {
                result.add("false");
            }
        }

        if (args.length == 2 && same(args[0], "droppable")) {
            if ("true".startsWith(args[1])) {
                result.add("true");
            }

            if ("false".startsWith(args[1])) {
                result.add("false");
            }
        }

        return result;
    }
}
