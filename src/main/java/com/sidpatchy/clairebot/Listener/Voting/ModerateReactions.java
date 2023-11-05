package com.sidpatchy.clairebot.Listener.Voting;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.MessageUtils;
import com.sidpatchy.clairebot.Util.Voting.VotingUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.util.HashMap;
import java.util.List;

public class ModerateReactions implements ReactionAddListener {

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        Message message = event.requestMessage().join();
        assert message != null;

        // Avoid executing past here when the author isn't the bot.
        if (!MessageUtils.isBotUser(message)) {
            Main.getLogger().debug("Ignoring reactions, not bot user.");
            return;
        }

        Embed embed = message.getEmbeds().get(0);
        EmbedFooter footer = embed.getFooter().orElse(null);
        assert footer != null;
        String footerText = footer.getText().orElse(null);

        if (footerText != null && footerText.contains("Poll ID")) {
            HashMap<String, String> pollID = VotingUtils.parsePollID(footerText.replace("Poll ID: ", ""));
            boolean allowMultipleChoices = pollID.get("allowMultipleChoices").equalsIgnoreCase("1");

            if (!allowMultipleChoices) {
                String emote = event.getEmoji().asUnicodeEmoji().orElse(null);
                User author = event.getUser().orElse(null);

                assert author != null;
                if (author.isYourself()) {
                    return;
                }

                List<Reaction> reacts = message.getReactions();

                for (Reaction reaction : reacts) {

                    if (emote != null && !emote.equalsIgnoreCase(reaction.getEmoji().asUnicodeEmoji().orElse(null))) {
                        reaction.removeUser(author);
                    }
                }
            }
        }
    }
}
