package me.aixo.axdiscord.discord;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CodeManager {
    public static final Map<UUID, String> playerCodes = new HashMap<>();


    public static Map<UUID, String> getPlayerCodes() {
        return playerCodes;
    }


    public static String generateCode(UUID playerUUID) {
        // Jeśli dla gracza istnieje kod, zwróć go
        if (playerCodes.containsKey(playerUUID)) {
            return playerCodes.get(playerUUID);
        }

        // W przeciwnym razie wygeneruj nowy kod
        String newCode = String.format("%04d", new Random().nextInt(10000));
        long currentTime = System.currentTimeMillis();
        playerCodes.put(playerUUID, newCode);
        return newCode;
    }

    public static void removeCodeForPlayer(UUID playerUUID) {
        playerCodes.remove(playerUUID);
    }
}


