package com.sidpatchy.clairebot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sidpatchy.Robin.Discord.Command;

/**
 * Represents ALL commands within ClaireBot. Initialized using the CommandFactory from Robin >= 2.1.0.
 */
public class Commands {
    private Command avatar;
    private Command config;
    private Command eightball;
    private Command help;
    private Command info;
    private Command leaderboard;
    private Command level;
    private Command poll;
    private Command quote;
    private Command request;
    private Command santa;
    private Command server;
    private Command user;
    @JsonProperty("config-revision")
    private String configRevision;

    public Command getAvatar() {
        return avatar;
    }

    public Command getConfig() {
        return config;
    }

    public Command getEightball() {
        return eightball;
    }

    public Command getHelp() {
        return help;
    }

    public Command getInfo() {
        return info;
    }

    public Command getLeaderboard() {
        return leaderboard;
    }

    public Command getLevel() {
        return level;
    }

    public Command getPoll() {
        return poll;
    }

    public Command getQuote() {
        return quote;
    }

    public Command getRequest() {
        return request;
    }

    public Command getSanta() {
        return santa;
    }

    public Command getServer() {
        return server;
    }

    public Command getUser() {
        return user;
    }

    public void setAvatar(Command avatar) {
        this.avatar = avatar;
    }

    public void setConfig(Command config) {
        this.config = config;
    }

    public void setEightball(Command eightball) {
        this.eightball = eightball;
    }

    public void setHelp(Command help) {
        this.help = help;
    }

    public void setInfo(Command info) {
        this.info = info;
    }

    public void setLeaderboard(Command leaderboard) {
        this.leaderboard = leaderboard;
    }

    public void setLevel(Command level) {
        this.level = level;
    }

    public void setPoll(Command poll) {
        this.poll = poll;
    }

    public void setQuote(Command quote) {
        this.quote = quote;
    }

    public void setRequest(Command request) {
        this.request = request;
    }

    public void setSanta(Command santa) {
        this.santa = santa;
    }

    public void setServer(Command server) {
        this.server = server;
    }

    public void setUser(Command user) {
        this.user = user;
    }
}
