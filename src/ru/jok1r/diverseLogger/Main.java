package ru.jok1r.diverseLogger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.jok1r.diverseLogger.listeners.NPCListener;
import ru.jok1r.diverseLogger.listeners.PlayerListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends JavaPlugin {

    public String takingItem, takingItemFromPlayer, droppingItem, killingItem, chestTaking, npcDrop, npcKilling;
    private FileWriter logWriter;

    final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new NPCListener(this), this);
        this.getCommand("dlogger").setExecutor(new CommandDiverseLogger(this));
        this.register();
    }

    public void register() {
        File cfg = new File(this.getDataFolder(), "config.yml");
        if(!cfg.exists()) {
            this.saveResource("config.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(cfg);
        this.takingItem = config.getString("messages.takingItem");
        this.takingItemFromPlayer = config.getString("messages.takingItemFromPlayer");
        this.droppingItem = config.getString("messages.droppingItem");
        this.killingItem = config.getString("messages.killingItem");
        this.chestTaking = config.getString("messages.chestTaking");
        this.npcDrop = config.getString("messages.npcDrop");
        this.npcKilling = config.getString("messages.npcKilling");
    }

    public String getTime() {
        Date date = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")).getTime();
        return date.getHours() + ":" + date.getMinutes() + ":"  + date.getSeconds() + "_" + date.getDay() + "-" + date.getMonth();
    }

    public void saveKilledData(final String npc, final String player) {
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(getDataFolder(), "statistics.yml");
                if(!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
                String npc_conf = npc.replaceAll(" ", "_");
                if(conf.getConfigurationSection("npc") == null) {
                    conf.set("npc." + npc_conf, 0);
                }

                int countKilled = conf.getInt("npc." + npc_conf);
                if(countKilled == 0) {
                    conf.set("npc." + npc_conf, 1);
                } else {
                    conf.set("npc." + npc_conf, countKilled + 1);
                }

                int countPlayer = conf.getInt("players." + player + "." + npc_conf);
                if(countPlayer == 0) {
                    conf.set("players." + player + "." + npc_conf, 1);
                } else {
                    conf.set("players." + player + "." + npc_conf, countPlayer + 1);
                }

                try {
                    conf.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logging(final String text) throws IOException {
        if(this.logWriter == null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
            Date date = calendar.getTime();
            File file = new File(this.getDataFolder(), "log");
            if(!file.exists()) {
                file.mkdir();
            }

            String name = date.getHours() + ":" + date.getMinutes() + "_" + date.getDay() + "-" + (date.getMonth() + 1);
            file = new File(file, name + ".log");
            file.createNewFile();
            this.logWriter = new FileWriter(file);
        }

        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logWriter.write(text);
                    logWriter.append('\n');
                    logWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
