package com.sidpatchy.clairebot.Listener.Voting;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.MessageUtils;
import com.sidpatchy.clairebot.Util.Voting.VotingUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;

public class ModerateReactions extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        Message message = event.retrieveMessage().complete();
        assert message != null;

        // Avoid executing past here when the author isn't the bot.
        if (!MessageUtils.isBotUser(message)) {
            Main.getLogger().debug("Ignoring reactions, not bot user.");
            return;
        }

        if (message.getEmbeds().isEmpty()) {
            return;
        }
        MessageEmbed embed = message.getEmbeds().get(0);
        MessageEmbed.Footer footer = embed.getFooter();
        if (footer == null) {
            return;
        }
        String footerText = footer.getText();

        if (footerText != null) {
            String encoded = VotingUtils.extractPollIdFromFooter(footerText);
            if (!encoded.isEmpty()) {
                HashMap<String, String> pollID = VotingUtils.parsePollID(encoded);
                boolean allowMultipleChoices = pollID.get("allowMultipleChoices").equalsIgnoreCase("1");

                if (!allowMultipleChoices) {
                    String emote = event.getEmoji().getType() == net.dv8tion.jda.api.entities.emoji.Emoji.Type.UNICODE ? event.getEmoji().getName() : null;
                    User author = event.getUser();

                    assert author != null;
                    if (author.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
                        return;
                    }

                    List<MessageReaction> reacts = message.getReactions();

                    for (MessageReaction reaction : reacts) {
                        if (emote != null && !emote.equalsIgnoreCase(reaction.getEmoji().getName())) {
                            message.removeReaction(reaction.getEmoji(), author).queue();
                        }
                    }
                }
            }
        }
    }
}
