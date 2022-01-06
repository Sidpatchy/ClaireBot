package com.sidpatchy.clairebot.File;

import com.sidpatchy.clairebot.Main;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Parse nested bits of the commands file.
 *
 * @author Sidpatchy
 */
public class ParseCommands {

    static ConfigReader config = new ConfigReader();
    private static final String commandsFile = Main.getCommandsFile();

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getCommand(String command) throws FileNotFoundException {
        Map<String , Object> obj = (Map<String, Object>) config.getObj(commandsFile, "commands");
        return (Map<String, Object>) obj.get(command);
    }

    public static String getCommandName(String command) throws FileNotFoundException {
        return (String) getCommand(command).get("name");
    }

    public static String getCommandUsage(String command) throws FileNotFoundException {
        return (String) getCommand(command).get("usage");
    }

    public static String getCommandHelp(String command) throws FileNotFoundException {
        return (String) getCommand(command).get("help");
    }
}
