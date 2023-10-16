package me.aixo.axdiscord.placeholder;

import me.aixo.axdiscord.database.DatabaseManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AXDiscordPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "AXDiscord";
    }

    @Override
    public String getAuthor() {
        return "aix0";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if ("sync_player".equals(identifier)) {
            return getSyncValue(player.getUniqueId());
        }

        return null;
    }

    private String getSyncValue(UUID uuid) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT sync FROM discord_sync WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int syncValue = resultSet.getInt("sync");
                return syncValue == 1 ? "1" : "0";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }
}