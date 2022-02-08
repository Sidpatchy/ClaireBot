package com.sidpatchy.clairebot;

import com.sidpatchy.clairebot.Util.GetYAMLFromURL;

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

        timer.schedule(task, 1000, 60000);
    }

    public static void setPhishingDomains(List<String> value) {phishingDomains = value;}
    public static List<String> getPhishingDomains() {return phishingDomains;}

}


class Helper extends TimerTask {

    GetYAMLFromURL config = new GetYAMLFromURL();

    @Override
    public void run() {
        Clockwork.setPhishingDomains(config.readYAMLFromURL("https://raw.githubusercontent.com/nikolaischunk/discord-phishing-links/main/domain-list.json"));
        Main.getLogger().info("Clockwork update");
    }
}