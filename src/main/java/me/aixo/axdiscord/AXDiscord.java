package me.aixo.axdiscord;

import me.aixo.axdiscord.commands.DiscordCommand;
import me.aixo.axdiscord.database.DatabaseManager;
import me.aixo.axdiscord.discord.DiscordBot;
import me.aixo.axdiscord.discord.RoleSynchronizer;
import me.aixo.axdiscord.listeners.LuckPermsListener;
import me.aixo.axdiscord.placeholder.AXDiscordPlaceholderExpansion;
import net.dv8tion.jda.api.JDA;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class AXDiscord extends JavaPlugin {
    private static AXDiscord instance;
    private static String botToken;
    private static String channelId;

    private static String guildId;
    private static JDA jda;

    private LuckPermsListener luckPermsListener;


    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        LuckPerms api = LuckPermsProvider.get();
        RoleSynchronizer roleSynchronizer = new RoleSynchronizer();
        luckPermsListener = new LuckPermsListener(roleSynchronizer);

        botToken = getConfig().getString("bot_token");
        channelId = getConfig().getString("channel_id");
        guildId = getConfig().getString("guild_id");

        DatabaseManager.initialize();
        // Inicjalizacja komendy /discord
        this.getCommand("discord").setExecutor(new DiscordCommand());
        // Inicjalizacja bota Discorda
        jda = DiscordBot.startBot();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AXDiscordPlaceholderExpansion().register();
        }
    }

    @Override
    public void onDisable() {
        DatabaseManager.close();
    }

    public static String getBotToken() {
        return botToken;
    }

    public static String getChannelId() {
        return channelId;
    }

    public static AXDiscord getInstance() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }

    public static String getGuildId() {
        return guildId;
    }

}
