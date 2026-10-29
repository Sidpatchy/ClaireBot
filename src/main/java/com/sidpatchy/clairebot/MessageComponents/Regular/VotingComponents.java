package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import org.javacord.api.entity.message.component.*;

import java.util.Arrays;

public class VotingComponents {

    // Question row
    public static ActionRow getQuestionRow(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.Question");
        return ActionRow.of(new TextInputBuilder(TextInputStyle.SHORT, "question-modal", label)
                .setRequired(true).setMaximumLength(250).build());
    }

    // Details row
    public static ActionRow getDetailsRow(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.Details");
        return ActionRow.of(new TextInputBuilder(TextInputStyle.PARAGRAPH, "details-modal", label)
                .setRequired(false).setMaximumLength(1000).build());
    }

    // Second Menu
    // Multiple choices row
    public static ActionRow getMultipleChoicesRow(LanguageManager languageManager, String commandName, String draftId) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleChoicePlaceholder");

        String yesLabel = languageManager.getLocalizedString("ClaireLang.Generic.Yes");
        String yesDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleChoiceYesDescription");

        String noLabel = languageManager.getLocalizedString("ClaireLang.Generic.No");
        String noDescTemplate = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleChoiceNoDescription");
        String noDesc = noDescTemplate.replace("{cb.commandname}", commandName);

        return ActionRow.of(
                SelectMenu.create("vote-choices:" + draftId, placeholder, 1, 1,
                        Arrays.asList(
                                // Keep values stable for interaction handlers
                                SelectMenuOption.create(yesLabel, "Yes", yesDesc),
                                SelectMenuOption.create(noLabel, "No", noDesc)
                        ))
        );
    }

    // Select menus belong on messages; text inputs belong in modals (up to five per modal).
    public static ActionRow getAllowMultipleChoicesRow(LanguageManager languageManager, String draftId) {
        String allowPlaceholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.AllowMultipleChoicesPlaceholder");

        String yesLabel = languageManager.getLocalizedString("ClaireLang.Generic.Yes");
        String yesDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.AllowMultipleChoicesYesDescription");

        String noLabel = languageManager.getLocalizedString("ClaireLang.Generic.No");
        String noDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.AllowMultipleChoicesNoDescription");

        return ActionRow.of(SelectMenu.create("vote-multi:" + draftId, allowPlaceholder, 1, 1,
                Arrays.asList(
                        SelectMenuOption.create(yesLabel, "Yes", yesDesc),
                        SelectMenuOption.create(noLabel, "No", noDesc)
                )));
    }

    public static ActionRow getOptionActionRow(int optionNumber, LanguageManager languageManager, boolean required) {
        String template = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.OptionLabelTemplate");
        String label = template.replace("{cb.voting.optionnumber}", String.valueOf(optionNumber));
        return new ActionRowBuilder()
                .addComponents(new TextInputBuilder(TextInputStyle.SHORT, "option-" + optionNumber, label)
                        .setRequired(required).setMaximumLength(90).build())
                .build();
    }
}
