//package me.aixo.axdiscord.discord;
//
//import me.aixo.axdiscord.AXDiscord;
//import org.bukkit.ChatColor;
//import org.bukkit.configuration.file.FileConfiguration;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ConfigManager {
//
//    private static ConfigManager instance;
//    private FileConfiguration config;
//    private String verifiedRoleId;
//    private String alreadySyncedMsg;
//    private String codeGeneratedMsg;
//    private String noPermissionsMsg;
//    private String incorrectUsage;
//    private String codeAcceptedMsgDiscord;
//    private String codeInvalidMsgDiscord;
//    private String codeInvalidOfflineMsgDiscord;
//    private String successfulSyncBroadcastMsg;
//    private Map<String, String> groupRoleSynchronization;
//    private List<String> commandsForLink;
//
//    public static ConfigManager getInstance() {
//        if (instance == null) {
//            instance = new ConfigManager();
//        }
//        return instance;
//    }
//
//    public void reload() {
//        AXDiscord.getInstance().reloadConfig();
//        config = AXDiscord.getInstance().getConfig();
//
//        verifiedRoleId = config.getString("verified_role_id");
//
//        codeAcceptedMsgDiscord = config.getString("messages.discord.code-accepted");
//        codeInvalidMsgDiscord = config.getString("messages.discord.code-invalid");
//        codeInvalidOfflineMsgDiscord = config.getString("messages.discord.code-acceptedOffline");
//
//        alreadySyncedMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.already-synced"));
//        codeGeneratedMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.code-generated"));
//        noPermissionsMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.no-permission"));
//        incorrectUsage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.incorrect-usage"));
//        successfulSyncBroadcastMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.successful-sync-broadcast"));
//        commandsForLink = config.getStringList("commands_for_link");
//
//        if (config.getConfigurationSection("GroupRoleSynchronization") != null) {
//            groupRoleSynchronization = new HashMap<>();
//            for (String key : config.getConfigurationSection("GroupRoleSynchronization").getKeys(false)) {
//                groupRoleSynchronization.put(key, config.getString("GroupRoleSynchronization." + key));
//            }
//        }
//    }
//
//
//    public FileConfiguration getConfig() {
//        return config;
//    }
//
//    public String getVerifiedRoleId() {
//        return verifiedRoleId;
//    }
//
//    public String getAlreadySyncedMsg() {
//        return alreadySyncedMsg;
//    }
//
//    public String getCodeGeneratedMsg() {
//        return codeGeneratedMsg;
//    }
//
//    public String getNoPermissionsMsg() {
//        return noPermissionsMsg;
//    }
//
//    public String getIncorrectUsage() {
//        return incorrectUsage;
//    }
//
//    public String getCodeAcceptedMsgDiscord() {
//        return codeAcceptedMsgDiscord;
//    }
//
//    public String getCodeInvalidMsgDiscord() {
//        return codeInvalidMsgDiscord;
//    }
//
//    public String getCodeInvalidOfflineMsgDiscord() {
//        return codeInvalidOfflineMsgDiscord;
//    }
//
//    public String getSuccessfulSyncBroadcastMsg() {
//        return successfulSyncBroadcastMsg;
//    }
//
//    public Map<String, String> getGroupRoleSynchronization() {
//        return groupRoleSynchronization;
//    }
//
//    public List<String> getCommandsForLink() {
//        return commandsForLink;
//    }
//
//    private ConfigManager() {
//        reload();
//    }
//}
