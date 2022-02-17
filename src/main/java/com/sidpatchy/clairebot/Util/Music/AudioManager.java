package com.sidpatchy.clairebot.Util.Music;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static Map<Long, ServerMusicManager> managers = new HashMap<>();

    public static ServerMusicManager get(Long serverID) {
        if (!managers.containsKey(serverID)) {
            managers.put(serverID, new ServerMusicManager(PlayerManager.getManager()));
        }

        return managers.get(serverID);
    }
}
