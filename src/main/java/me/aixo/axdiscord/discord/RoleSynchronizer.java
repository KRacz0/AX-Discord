package me.aixo.axdiscord.discord;

import me.aixo.axdiscord.AXDiscord;
import me.aixo.axdiscord.database.DatabaseManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoleSynchronizer {

    private static LuckPerms luckPerms;

    public RoleSynchronizer() {
        // Inicjalizacja LuckPerms
        luckPerms = Bukkit.getServicesManager().load(LuckPerms.class);
    }

    public void synchronizeRolesWithDiscord(Member member, UUID playerUUID) {
        // Pobieranie mapy rang z konfiguracji
        Map<String, Object> configMap = AXDiscord.getInstance().getConfig().getConfigurationSection("GroupRoleSynchronization").getValues(false);

        // Sprawdzenie czy mapa konfiguracji jest pusta
        if (configMap == null || configMap.isEmpty()) {
            AXDiscord.getInstance().getLogger().severe("Nie udało się wczytać mapy rang z konfiguracji.");
            return;
        }

        Map<String, String> groupRoleSyncMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            groupRoleSyncMap.put(entry.getKey(), entry.getValue().toString());
        }

        // Sprawdzenie czy użytkownik jest zsynchronizowany
        if (!DatabaseManager.isPlayerInDatabase(playerUUID)) {
            return;
        }

        // Sprawdzenie czy ID Discorda istnieje
        String discordId = DatabaseManager.getDiscordIdByUUID(playerUUID);
        if (discordId == null || discordId.isEmpty()) {
            return;
        }



        // Pobieranie obiektu użytkownika z LuckPerms
        net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(playerUUID);
        if (user == null) {
            AXDiscord.getInstance().getLogger().severe("Nie można znaleźć użytkownika w LuckPerms.");
            return;
        }


        for (InheritanceNode groupNode : user.getNodes(NodeType.INHERITANCE)) {
            String groupName = groupNode.getGroupName();

            // Jeśli ranga istnieje w configu
            if (groupRoleSyncMap.containsKey(groupName)) {
                String roleId = groupRoleSyncMap.get(groupName);
                if (roleId == null || roleId.isEmpty()) {
                    continue;
                }
                Role role = member.getGuild().getRoleById(roleId);

                // Jeśli rola istnieje i gracz jej nie ma
                if (role != null && !member.getRoles().contains(role)) {
                    member.getGuild().addRoleToMember(member, role).queue(success -> {
                        AXDiscord.getInstance().getLogger().info("Pomyślnie dodano rolę " + role.getName() + " dla użytkownika " + member.getEffectiveName());
                    }, failure -> {
                        AXDiscord.getInstance().getLogger().info("Nie udało się dodać roli. " + roleId +  " Błąd: " + failure.getMessage());
                    });
                }
            }
        }
        // Usuwanie kodu weryfikacyjnego po pomyślnej synchronizacji ról
        CodeManager.removeCodeForPlayer(playerUUID);
    }

    public void removeRolesFromDiscord(Member member, UUID playerUUID) {
        Map<String, Object> configMap = AXDiscord.getInstance().getConfig().getConfigurationSection("GroupRoleSynchronization").getValues(false);
        Map<String, String> groupRoleSyncMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            groupRoleSyncMap.put(entry.getKey(), entry.getValue().toString());
        }

        // Sprawdzenie czy użytkownik jest zsynchronizowany
        if (!DatabaseManager.isPlayerInDatabase(playerUUID)) {
            return;
        }

        // Sprawdzenie czy ID Discorda istnieje
        String discordId = DatabaseManager.getDiscordIdByUUID(playerUUID);
        if (discordId == null || discordId.isEmpty()) {
            return;
        }

        net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(playerUUID);
        if (user == null) {
            AXDiscord.getInstance().getLogger().info("Nie można znaleźć użytkownika w LuckPerms.");
            return;
        }

        for (Map.Entry<String, String> entry : groupRoleSyncMap.entrySet()) {
            String groupName = entry.getKey();
            String roleId = entry.getValue();
            Role role = member.getGuild().getRoleById(roleId);

            if (role != null && member.getRoles().contains(role)) {
                if (!user.getNodes(NodeType.INHERITANCE).stream().anyMatch(node -> node.getGroupName().equalsIgnoreCase(groupName))) {
                    member.getGuild().removeRoleFromMember(member, role).queue(success -> {
                        AXDiscord.getInstance().getLogger().info("Pomyślnie usunięto rolę " + role.getName() + " dla użytkownika " + member.getEffectiveName());
                    }, failure -> {
                        AXDiscord.getInstance().getLogger().info("Nie udało się usunąć roli. " + roleId +  " Błąd: " + failure.getMessage());
                    });
                }
            }
        }
    }

}





