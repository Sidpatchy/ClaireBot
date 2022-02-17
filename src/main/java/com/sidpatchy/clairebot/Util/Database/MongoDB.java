package com.sidpatchy.clairebot.Util.Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sidpatchy.clairebot.File.ConfigReader;
import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;
import org.bson.Document;
import org.javacord.api.DiscordApi;

/**
 * Class to abstract out database calls
 */
public class MongoDB {

    private static final Logger logger = Main.getLogger();
    private static final DiscordApi api = Main.getApi();
    private static final ConfigReader config = new ConfigReader();

    private static final String mongodb_uri = config.getString(Main.getConfigFile(), "mongodb_uri");
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> server;

    public static void connect(String dbName) {

        logger.info("Connecting to MongoDB...");

        try {
            mongoClient = MongoClients.create(mongodb_uri);
            database = mongoClient.getDatabase(dbName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to connect to the database " + dbName + " @ " + mongodb_uri);
        }
    }

    public static void closeConnection() {
        mongoClient.close();
    }

    public static void addServer(String guildID) {
        database.createCollection(guildID);
    }

    public static void getServer(String guildID) {
        try {
            server = database.getCollection(guildID);
        }
        catch (Exception e) {
            logger.debug(e);
            addServer(guildID);
            getServer(guildID);
        }
    }

    public static void getConfigOption() {

    }
}
