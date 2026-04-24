package com.sidpatchy.clairebot.Lang;

import com.sidpatchy.Robin.Exception.InvalidConfigurationException;
import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LanguageManager {

    private final static Logger logger = Main.getLogger();

    private final String pathToLanguageFiles;
    private final Locale fallbackLocale;
    private final Server server;
    private final User user;
    private final ContextManager context;
    private final PlaceholderHandler placeholderHandler;

    /**
     * The LanguageManager class is responsible for loading and managing language files based on user preferences.
     * It provides methods to retrieve localized strings and get the language file based on the user's preferred language.
     * <p>
     * If the language manager is being used in the context of a Server, a server MUST be specified in order to conform
     * to the ClaireLang and ClaireConfig specifications. It is safe to pass a null value for the server via
     * ContextManager as ClaireLang will automatically interpret this as there being no server.
     */
    public LanguageManager(String pathToLanguageFiles,
                           Locale fallbackLocale,
                           ContextManager context) {
        this.pathToLanguageFiles = Main.getTranslationsPath();
        this.context = context;
        this.fallbackLocale = fallbackLocale;

        this.server = context.server();
        this.user = context.user();
        this.placeholderHandler = new PlaceholderHandler(context);
    }

    /**
     * The LanguageManager class is responsible for loading and managing language files based on user preferences.
     * It provides methods to retrieve localized strings and get the language file based on the user's preferred language.
     * <p>
     * If the language manager is being used in the context of a Server, a server MUST be specified in order to conform
     * to the ClaireLang and ClaireConfig specifications. It is safe to pass a null value for the server via
     * ContextManager as ClaireLang will automatically interpret this as there being no server.
     * <p>
     * <b>This signature should only be used by ClaireBot. If you are developing a plugin, you must specify a different
     * locale path unless you are referencing ClaireBot's builtin language strings. The standard path can be obtained
     * through the plugin API.</b>
     */
    public LanguageManager(Locale fallbackLocale, ContextManager context) {
        this.pathToLanguageFiles = Main.getTranslationsPath();
        this.context = context;
        this.fallbackLocale = fallbackLocale;

        this.server = context.server();
        this.user = context.user();
        this.placeholderHandler = new PlaceholderHandler(context);
    }

    /**
     * Retrieves the localized string corresponding to the given key.
     *
     * @param key the key for the desired localized string
     * @return the localized string if found, otherwise returns the key itself
     * @throws IOException if an I/O error occurs while retrieving the localized string
     */
    public String getLocalizedString(String key) {
        Locale locale = resolveEffectiveLocale(server, user);
        // Load primary and fallback separately to support per-key fallback
        RobinConfiguration primary = tryLoadConfig(new File(pathToLanguageFiles, "lang_" + locale.toLanguageTag() + ".yml"));
        RobinConfiguration fallback = tryLoadConfig(new File(pathToLanguageFiles, "lang_" + fallbackLocale.toLanguageTag() + ".yml"));

        String localized = null;
        if (primary != null) {
            localized = primary.getString(key);
        }
        if (localized == null && fallback != null) {
            localized = fallback.getString(key);
        }
        String raw = localized != null ? localized : key;
        return placeholderHandler.process(raw);
    }

    /**
     * Retrieves a localized string based on the provided base path and key.
     *
     * @param basePath the base path used to locate the language file or namespace
     * @param key the key for the desired localized string
     * @return the localized string if found, otherwise returns the concatenation of basePath and key
     */
    public String getLocalizedString(String basePath, String key) {
        return getLocalizedString(basePath + "." + key);
    }

    /**
     * Retrieves the localized string corresponding to the given key.
     *
     * @param key the key for the desired localized string
     * @return the localized string if found, otherwise returns the key itself
     * @throws IOException if an I/O error occurs while retrieving the localized string
     */
    public List<String> getLocalizedList(String key) {
        Locale locale = resolveEffectiveLocale(server, user);
        RobinConfiguration primary = tryLoadConfig(new File(pathToLanguageFiles, "lang_" + locale.toLanguageTag() + ".yml"));
        RobinConfiguration fallback = tryLoadConfig(new File(pathToLanguageFiles, "lang_" + fallbackLocale.toLanguageTag() + ".yml"));

        List<String> localizedList = null;
        if (primary != null) {
            localizedList = primary.getList(key, String.class);
        }
        if (localizedList == null && fallback != null) {
            localizedList = fallback.getList(key, String.class);
        }
        List<String> rawLanguageString = localizedList != null ? localizedList : List.of(key);
        return placeholderHandler.process(rawLanguageString);
    }

    /**
     * Retrieves a localized list of strings based on the provided base path and key.
     *
     * @param basePath the base path used to locate the language file or namespace
     * @param key      the key for the desired localized list of strings
     * @return a list of localized strings if found, otherwise returns a list containing the concatenation of basePath and key
     */
    public List<String> getLocalizedList(String basePath, String key) {
        return getLocalizedList(basePath + "." + key);
    }

    private Locale resolveEffectiveLocale(Server server, User user) {
        Locale locale;
        try {
            if (user == null) {
                locale = fallbackLocale;
            } else {
                APIUser apiUser = new APIUser(user.getIdAsString());
                apiUser.getUser();
                String rawLang = apiUser.getLanguage();

                if (rawLang == null || rawLang.isBlank()) {
                    locale = fallbackLocale;
                } else {
                    String normalizedTag = rawLang.replace('_', '-');
                    locale = Locale.forLanguageTag(normalizedTag);
                    if (locale == null || locale.toLanguageTag().equals("und")) {
                        locale = fallbackLocale;
                    }
                }
            }

            if (server != null) {
                Guild guild = new Guild(server.getIdAsString());
                guild.getGuild();

                if (guild.isEnforceSeverLanguage()) {
                    String guildLocaleTag = guild.getLocale();
                    if (guildLocaleTag != null && !guildLocaleTag.isBlank()) {
                        String normalized = guildLocaleTag.replace('_', '-');
                        Locale parsed = Locale.forLanguageTag(normalized);
                        if (parsed != null && !"und".equals(parsed.toLanguageTag())) {
                            locale = parsed;
                        } else if (server.getPreferredLocale() != null) {
                            locale = server.getPreferredLocale();
                        } else {
                            locale = fallbackLocale;
                        }
                    } else if (server.getPreferredLocale() != null) {
                        locale = server.getPreferredLocale();
                    } else {
                        locale = fallbackLocale;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("ClaireData failed to return a response for Locale information. Are we cooked?");
            locale = fallbackLocale;
        }
        return locale;
    }

    private RobinConfiguration parseUserAndServerOptions(Server server, User user) {
        Locale locale;
        try {
            // If user is null (e.g., triggered by a system action or missing context),
            // default to fallback locale and continue to evaluate server-level overrides.
            if (user == null) {
                locale = fallbackLocale;
            } else {
                APIUser apiUser = new APIUser(user.getIdAsString());
                apiUser.getUser();
                String rawLang = apiUser.getLanguage();

                // Normalize ClaireData language strings (e.g., en_US -> en-US). If empty/null, use fallback.
                if (rawLang == null || rawLang.isBlank()) {
                    locale = fallbackLocale;
                } else {
                    String normalizedTag = rawLang.replace('_', '-');
                    locale = Locale.forLanguageTag(normalizedTag);
                    // Guard against Locale.ROOT ("und") resulting from invalid tags
                    if (locale == null || locale.toLanguageTag().equals("und")) {
                        locale = fallbackLocale;
                    }
                }
            }

            // todo, pending ClaireData update: allow server admins to specify a custom language string.
            // todo ref https://trello.com/c/vkQTCTMG
            if (server != null) {
                Guild guild = new Guild(server.getIdAsString());
                guild.getGuild();

                if (guild.isEnforceSeverLanguage()) {
                    // Respect stored guild locale when enforcement is enabled.
                    String guildLocaleTag = guild.getLocale();
                    if (guildLocaleTag != null && !guildLocaleTag.isBlank()) {
                        String normalized = guildLocaleTag.replace('_', '-');
                        Locale parsed = Locale.forLanguageTag(normalized);
                        if (parsed != null && !"und".equals(parsed.toLanguageTag())) {
                            locale = parsed;
                        } else if (server.getPreferredLocale() != null) {
                            locale = server.getPreferredLocale();
                        } else {
                            locale = fallbackLocale;
                        }
                    } else if (server.getPreferredLocale() != null) {
                        // Fallback to Discord server preferred locale if guild setting is absent
                        locale = server.getPreferredLocale();
                    } else {
                        locale = fallbackLocale;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("ClaireData failed to return a response for Locale information. Are we cooked?");
            locale = fallbackLocale;
        }

        return getLangFileByLocale(locale);
    }

    /**
     * Returns a file based off the language string specified.
     * Returns the fallback file if a suitable translation isn't found.
     *
     * @param locale the Locale object for the bot
     * @return Returns a localized language file or the fallback file if a suitable translation doesn't exist.
     */
    public RobinConfiguration getLangFileByLocale(Locale locale) {
        File targetFile = new File(pathToLanguageFiles, "lang_" + locale.toLanguageTag() + ".yml");
        File fallbackFile = new File(pathToLanguageFiles, "lang_" + fallbackLocale.toLanguageTag() + ".yml");

        // Try primary file
        RobinConfiguration config = tryLoadConfig(targetFile);
        if (config != null) {
            return config;
        }

        // Try fallback file
        config = tryLoadConfig(fallbackFile);
        if (config != null) {
            logger.warn("Using fallback language file for locale: {}", locale);
            return config;
        }

        // Ultimate fallback - empty config
        logger.error("All language files failed to load! Using empty configuration.");
        return new RobinConfiguration();
    }

    private RobinConfiguration tryLoadConfig(File file) {
        try {
            RobinConfiguration config = new RobinConfiguration(file.getAbsolutePath());
            config.load();
            return config;
        } catch (InvalidConfigurationException e) {
            logger.error("Failed to load language file {}: {}", file, e.getMessage());
            return null;
        }
    }

    public void addContext(ContextManager.ContextType contextType, String key, Object Data) {
        context.addData(contextType, key, Data);
    }
}
