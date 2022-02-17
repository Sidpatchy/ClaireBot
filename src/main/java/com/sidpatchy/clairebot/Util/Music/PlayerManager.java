package com.sidpatchy.clairebot.Util.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

public class PlayerManager {

    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();

    public static void initManager() {
        manager.registerSourceManager(new YoutubeAudioSourceManager(true));
    }

    public static AudioPlayerManager getManager() {
        return  manager;
    }
}
