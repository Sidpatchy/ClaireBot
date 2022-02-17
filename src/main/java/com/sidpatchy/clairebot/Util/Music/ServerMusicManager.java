package com.sidpatchy.clairebot.Util.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class ServerMusicManager {

    public AudioPlayer player;
    public TrackScheduler scheduler;

    public ServerMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }
}
