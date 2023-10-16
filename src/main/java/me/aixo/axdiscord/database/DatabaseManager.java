package me.aixo.axdiscord.database;

import java.sql.*;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.aixo.axdiscord.AXDiscord;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static HikariDataSource dataSource;
    public static void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + AXDiscord.getInstance().getConfig().getString("database.host") + ":" + AXDiscord.getInstance().getConfig().getString("database.port") + "/" + AXDiscord.getInstance().getConfig().getString("database.name"));
        config.setUsername(AXDiscord.getInstance().getConfig().getString("database.username"));
        config.setPassword(AXDiscord.getInstance().getConfig().getString("database.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
        setupTable();
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    public static void setupTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS `discord_sync` (" +
                    "uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                    "minecraft_name VARCHAR(16) NOT NULL," +
                    "discord_id VARCHAR(32) NOT NULL," +
                    "sync INT DEFAULT NULL" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlayerSynced(UUID minecraftUUID) {
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM discord_sync WHERE uuid = ?");
            checkStatement.setString(1, minecraftUUID.toString());
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();
            checkStatement.close();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void savePlayerInfo(UUID minecraftUUID, String minecraftName, String discordId) {
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO discord_sync (uuid, minecraft_name, discord_id, sync) VALUES (?, ?, ?, 1)");
            insertStatement.setString(1, minecraftUUID.toString());
            insertStatement.setString(2, minecraftName);
            insertStatement.setString(3, discordId);
            insertStatement.executeUpdate();
            insertStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getDiscordIdByUUID(UUID minecraftUUID) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT discord_id FROM discord_sync WHERE uuid = ?");
            statement.setString(1, minecraftUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("discord_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isPlayerInDatabase(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM discord_sync WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }


}