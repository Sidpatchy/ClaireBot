package com.sidpatchy.clairebot.Listener.Voting;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.MessageUtils;
import com.sidpatchy.clairebot.Util.VotingUtils;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.List;

public class AddReactions implements MessageCreateListener {

    Logger logger = Main.getLogger();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        List<String> reactions = Main.getVoteEmoji();
        Message message = event.getMessage();

        // Avoid executing past here when the author isn't the bot.
        if (!MessageUtils.isBotUser(message)) {
            Main.getLogger().debug("Ignoring reactions, not bot user.");
            return;
        }

        Embed embed = null;
        try {
            embed = message.getEmbeds().get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.getLogger().error(e.getMessage());
            return;
        }
        if (embed == null) { return; }
        EmbedFooter footer = embed.getFooter().orElse(null);

        if (footer == null) {
            return;
        }

        String footerText = footer.getText().orElse(null);

        if (footerText != null && footerText.contains("Poll ID")) {
            HashMap<String, String> pollID = VotingUtils.parsePollID(footerText.replace("Poll ID: ", ""));
            int numChoices = Integer.parseInt(pollID.get("numChoices"));

            if (numChoices == 0) {
                message.addReaction("\uD83D\uDC4D");
                message.addReaction("\uD83D\uDC4E");
                message.addReaction(":vote:706373563564949566");
            }
            else {
                for (int i = 0; i < numChoices; i++) {
                    message.addReaction(reactions.get(i));
                }
            }
        }
    }
}
