package com.sidpatchy.clairebot.File;

import com.sidpatchy.clairebot.Main;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Parse nested bits of the commands file.
 *
 * @author Sidpatchy
 */
public class ParseCommands {

    static ConfigReader config = new ConfigReader();
    private static final String commandsFile = Main.getCommandsFile();

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getCommand(String command) {
        try {
            Map<String , Object> obj = (Map<String, Object>) config.getObj(commandsFile, "commands");
            return (Map<String, Object>) obj.get(command);
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.getLogger().error("Unable to read " + command + " from commands file");
            return null;
        }
    }

    public static String getCommandName(String command) {
        return (String) Objects.requireNonNull(getCommand(command)).get("name");
    }

    public static String getCommandUsage(String command) {
        return (String) Objects.requireNonNull(getCommand(command)).get("usage");
    }

    public static String getCommandHelp(String command) {
        return (String) Objects.requireNonNull(getCommand(command)).get("help");
    }

    public static HashMap<String, String> get(String command) {
        return new HashMap<String, String>() {{
            put("name", getCommandName(command));
            put("usage", getCommandUsage(command));
            put("help", getCommandHelp(command));
        }};
    }
}
