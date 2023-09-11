package ru.jok1r.diverseLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandDiverseLogger implements CommandExecutor {

    private final Main plugin;

    public CommandDiverseLogger(Main main) {
        this.plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.isOp()) {
            this.plugin.sendMessage(sender, "&7>> Недостаточно прав для использования этой команды");
            return false;
        }

        if(args.length < 1) {
            this.plugin.sendMessage(sender, "&c>> DiverseLogger: &7/dlogger <player>/<npc>");
            return false;
        }

        if(args[0].equalsIgnoreCase("player")) {
            File file = new File(this.plugin.getDataFolder(), "statistics.yml");
            if(!file.exists()) {
                this.plugin.sendMessage(sender, "&c>> А даты то и нет никакой(");
                return false;
            }

            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if(configuration.getConfigurationSection("players." + args[1]) == null) {
                this.plugin.sendMessage(sender, "&7>> Статистика игрока &c" + args[1] + " &7не найдена!");
                return false;
            }

            Map<String, Integer> data = new HashMap<String, Integer>();
            for(String npc : configuration.getConfigurationSection("players." + args[1]).getKeys(false)) {
                data.put(npc, configuration.getInt("players." + args[1] + "." + npc));
            }

            Map<String, Integer> topTen =
                    data.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(10)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            this.plugin.sendMessage(sender, "&7>> Статистика игрока &c" + args[1] + "");
            int i = 1;
            for(String name : topTen.keySet()) {
                this.plugin.sendMessage(sender, "&7" + i + ". &c" + name.replaceAll("_", " ") + "&7, кол-во: &c" + topTen.get(name));
                i++;
            }
            return false;
        }

        if(args[0].equalsIgnoreCase("npc")) {

            return false;
        }
        return false;
    }
}
