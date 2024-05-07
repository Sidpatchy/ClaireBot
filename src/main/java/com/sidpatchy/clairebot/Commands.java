package com.sidpatchy.clairebot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sidpatchy.Robin.Discord.Command;
import com.sidpatchy.Robin.Discord.CommandBuilder;

import java.util.Objects;

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
        validateCommand(avatar);
        return avatar;
    }

    public Command getConfig() {
        validateCommand(config);
        return config;
    }

    public Command getEightball() {
        validateCommand(eightball);
        return eightball;
    }

    public Command getHelp() {
        validateCommand(help);
        return help;
    }

    public Command getInfo() {
        validateCommand(info);
        return info;
    }

    public Command getLeaderboard() {
        validateCommand(leaderboard);
        return leaderboard;
    }

    public Command getLevel() {
        validateCommand(level);
        return level;
    }

    public Command getPoll() {
        validateCommand(poll);
        return poll;
    }

    public Command getQuote() {
        validateCommand(quote);
        return quote;
    }

    public Command getRequest() {
        validateCommand(request);
        return request;
    }

    public Command getSanta() {
        validateCommand(santa);
        return santa;
    }

    public Command getServer() {
        validateCommand(server);
        return server;
    }

    public Command getUser() {
        validateCommand(user);
        return user;
    }

    public String getConfigRevision() {
        return configRevision.isEmpty() ? null : configRevision;
    }

    protected void validateCommand(Command command) {
        Objects.requireNonNull(command, "Command cannot be null");
        Objects.requireNonNull(command.getName(), "Command name cannot be null");
        Objects.requireNonNull(command.getUsage(), "Command usage cannot be null");
        Objects.requireNonNull(command.getHelp(), "Command help cannot be null");

        if (command.getName().isEmpty() || command.getUsage().isEmpty() || command.getHelp().isEmpty()) {
            throw new IllegalArgumentException("Command name or usage cannot be empty");
        }

        // If command overview is null, set it to command help
        if (command.getOverview() == null || command.getOverview().isEmpty()) {
            command.setOverview(command.getHelp());
        }
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

    public void setConfigRevision(String configRevision) {
        this.configRevision = configRevision;
    }
}
