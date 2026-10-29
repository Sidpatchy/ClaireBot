package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import org.javacord.api.entity.message.component.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VotingComponents {

    // Question row
    public static ActionRow getQuestionRow(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.Question");
        return ActionRow.of(TextInput.create(TextInputStyle.SHORT, "question-modal", label));
    }

    // Details row
    public static ActionRow getDetailsRow(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.Details");
        return ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "details-modal", label));
    }

    // Second Menu
    // Multiple choices row
    public static ActionRow getMultipleChoicesRow(LanguageManager languageManager, String commandName) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleChoicePlaceholder");

        String yesLabel = languageManager.getLocalizedString("ClaireLang.Generic.Yes");
        String yesDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleChoiceYesDescription");

        String noLabel = languageManager.getLocalizedString("ClaireLang.Generic.No");
        String noDescTemplate = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleChoiceNoDescription");
        String noDesc = noDescTemplate.replace("{cb.commandname}", commandName);

        return ActionRow.of(
                SelectMenu.create("multiple-choice", placeholder, 1, 1,
                        Arrays.asList(
                                // Keep values stable for interaction handlers
                                SelectMenuOption.create(yesLabel, "Yes", yesDesc),
                                SelectMenuOption.create(noLabel, "No", noDesc)
                        ))
        );
    }

    // Third Menu, if chosen
    public static List<ActionRow> getSecondMenu(LanguageManager languageManager) {
        List<ActionRow> actionRows = new ArrayList<>();

        // Allow selecting multiple choices?
        String allowPlaceholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.AllowMultipleChoicesPlaceholder");

        String yesLabel = languageManager.getLocalizedString("ClaireLang.Generic.Yes");
        String yesDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.AllowMultipleChoicesYesDescription");

        String noLabel = languageManager.getLocalizedString("ClaireLang.Generic.No");
        String noDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.AllowMultipleChoicesNoDescription");

        actionRows.add(
                new ActionRowBuilder()
                        .addComponents(
                                SelectMenu.create("allow-multiple-choices", allowPlaceholder, 1, 1,
                                        Arrays.asList(
                                                // Keep values stable for interaction handlers
                                                SelectMenuOption.create(yesLabel, "Yes", yesDesc),
                                                SelectMenuOption.create(noLabel, "No", noDesc)
                                        ))
                        ).build()
        );

        // Populate options rows (0-9 to match existing behavior)
        for (int i = 0; i < 10; i++) {
            actionRows.add(getOptionActionRow(i, languageManager));
        }

        return actionRows;
    }

    public static ActionRow getOptionActionRow(int optionNumber, LanguageManager languageManager) {
        String template = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.OptionLabelTemplate");
        String label = template.replace("{cb.voting.optionnumber}", String.valueOf(optionNumber));
        return new ActionRowBuilder()
                .addComponents(TextInput.create(TextInputStyle.SHORT, "option-" + optionNumber, label))
                .build();
    }
}
