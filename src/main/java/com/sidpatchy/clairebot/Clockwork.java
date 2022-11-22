package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Util.GetYAMLFromURL;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to schedule out tasks that need to occur at regular intervals
 */
public class Clockwork {

    private static List<String> phishingDomains;

    public static Clockwork obj;

    public static void initClockwork() {
        obj = new Clockwork();
        Timer timer = new Timer();
        TimerTask task = new Helper();

        timer.schedule(task, 1000, 120000);
    }

    public static void setPhishingDomains(List<String> value) {phishingDomains = value;}
    public static List<String> getPhishingDomains() {return phishingDomains;}

}

@SuppressWarnings("unchecked")
class Helper extends TimerTask {

    GetYAMLFromURL config = new GetYAMLFromURL();

    @Override
    public void run() {
        try {
            Clockwork.setPhishingDomains((List<String>) config.getYAMLFromURL("https://raw.githubusercontent.com/nikolaischunk/discord-phishing-links/main/domain-list.json").get("domains"));
        } catch (IOException e) {
            Main.getLogger().error("Unable to reach phishing domains database.");
        }
        Main.getLogger().debug("Clockwork ticked");
    }
}