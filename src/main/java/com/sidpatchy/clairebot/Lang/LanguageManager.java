package com.sidpatchy.clairebot.Lang;

import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class LanguageManager {

    private final static Logger logger = Main.getLogger();

    private final String pathToLanguageFiles;
    private final Locale fallbackLocale;
    private final Server server;
    private final User user;

    /**
     * The LanguageManager class is responsible for loading and managing language files based on user preferences.
     * It provides methods to retrieve localized strings and get the language file based on the user's preferred language.
     * <p>
     * If the language manager is NOT being used in the context of a Server, but instead is being used in direct
     * messages or similar, server must be set to null. There is another constructor signature that handles this,
     * but it is best to avoid whenever possible.
     */
    public LanguageManager(String pathToLanguageFiles,
                           Locale fallbackLocaleString,
                           Server server,
                           User user) {
        this.pathToLanguageFiles = pathToLanguageFiles;
        this.fallbackLocale = fallbackLocaleString;
        this.server = server;
        this.user = user;
    }

    /**
     * The LanguageManager class is responsible for loading and managing language files based on user preferences.
     * It provides methods to retrieve localized strings and get the language file based on the user's preferred language.
     * <p>
     * If the language manager is being used in the context of a Server, a server MUST be specified in order to conform
     * to the ClaireLang and ClaireConfig specifications. There is another constructor signature that handles this. It
     * is safe to pass a null value for the server as ClaireLang will automatically interpret this as there being no
     * server.
     */
    public LanguageManager(String pathToLanguageFiles,
                           Locale fallbackLocaleString,
                           User user) {
        this.pathToLanguageFiles = pathToLanguageFiles;
        this.fallbackLocale = fallbackLocaleString;
        this.server = null;
        this.user = user;
    }

    /**
     * The LanguageManager class is responsible for loading and managing language files based on user preferences.
     * It provides methods to retrieve localized strings and get the language file based on the user's preferred language.
     * <p>
     * If the language manager is NOT being used in the context of a Server, but instead is being used in direct
     * messages or similar, server must be set to null. There is another constructor signature that handles this,
     * but it is best to avoid whenever possible.
     * <p>
     * This signature should only be used by ClaireBot. If you are developing a plugin, you must specify a different
     * locale path. The standard path can be obtained through the plugin API.
     */
    public LanguageManager(Locale fallbackLocaleString,
                           Server server,
                           User user) {
        this.pathToLanguageFiles = Main.getTranslationsPath();
        this.fallbackLocale = fallbackLocaleString;
        this.server = server;
        this.user = user;
    }

    /**
     * The LanguageManager class is responsible for loading and managing language files based on user preferences.
     * It provides methods to retrieve localized strings and get the language file based on the user's preferred language.
     * <p>
     * If the language manager is being used in the context of a Server, a server MUST be specified in order to conform
     * to the ClaireLang and ClaireConfig specifications. There is another constructor signature that handles this. It
     * is safe to pass a null value for the server as ClaireLang will automatically interpret this as there being no
     * server.
     * <p>
     * This signature should only be used by ClaireBot. If you are developing a plugin, you must specify a different
     * locale path. The standard path can be obtained through the plugin API.
     */
    public LanguageManager(Locale fallbackLocaleString,
                           User user) {
        this.pathToLanguageFiles = Main.getTranslationsPath();
        this.fallbackLocale = fallbackLocaleString;
        this.server = null;
        this.user = user;
    }

    /**
     * Retrieves the localized string corresponding to the given key.
     *
     * @param key the key for the desired localized string
     * @return the localized string if found, otherwise returns the key itself
     * @throws IOException if an I/O error occurs while retrieving the localized string
     */
    // todo consider handling the exception in this method, or in a different method signature.
    public String getLocalizedString(String key) throws IOException {
        APIUser apiUser = new APIUser(user.getIdAsString());
        apiUser.getUser();
        Locale locale = Locale.forLanguageTag(apiUser.getLanguage());

        // todo, pending ClaireData update: allow server admins to specify a custom language string.
        // todo ref https://trello.com/c/vkQTCTMG
        if (server != null) {
            Guild guild = new Guild(server.getIdAsString());
            guild.getGuild();

            if (guild.isEnforceSeverLanguage()) {
                // todo this should not be determined here, but will be until the ClaireData implementation is completed.
                // todo this should instead be determined when the Guild object is created in the database.
                // todo ClaireData update on hold while still designing the major ClaireBot update that follows this one.
                locale = server.getPreferredLocale();
            }
        }

        RobinConfiguration languageFile = getLangFileByLocale(locale);
        String localizedString = languageFile.getString(key);
        logger.debug(localizedString);
        return localizedString != null ? localizedString : key;
    }

    /**
     * Returns a file based off the language string specified.
     * Returns the fallback file if a suitable translation isn't found.
     *
     * @param locale the Locale object for the bot
     * @return Returns a localized language file or the fallback file if a suitable translation doesn't exist.
     */
    public RobinConfiguration getLangFileByLocale(Locale locale) {
        File file = new File(pathToLanguageFiles, "lang_" + locale.toLanguageTag() + ".yml");
        if (file.exists()) {
            return new RobinConfiguration(pathToLanguageFiles + "lang_" + locale.toLanguageTag() + ".yml");
        }
        else {
            return new RobinConfiguration(pathToLanguageFiles + "lang_" + fallbackLocale.toLanguageTag() + ".yml");
        }
    }
}
