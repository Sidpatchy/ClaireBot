package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Lang.ContextManager;
import org.javacord.api.entity.message.component.*;

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

    public static ActionRow getChoicesButtons(LanguageManager languageManager, String draftId) {
        String customLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.CustomChoicesButton");
        String thumbsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.ThumbsPollButton");
        return ActionRow.of(Button.primary("vote-choices-yes:" + draftId, customLabel),
                Button.secondary("vote-choices-no:" + draftId, thumbsLabel),
                Button.danger("vote-cancel:" + draftId, languageManager.getLocalizedString(
                        "ClaireLang.Embed.Commands.Components.Regular.Voting.Cancel")));
    }

    public static ActionRow getAllowMultipleChoicesButtons(LanguageManager languageManager, String draftId) {
        String multiLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.MultipleSelectionButton");
        String singleLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.SingleSelectionButton");
        return ActionRow.of(Button.primary("vote-multi-yes:" + draftId, multiLabel),
                Button.secondary("vote-multi-no:" + draftId, singleLabel),
                Button.danger("vote-cancel:" + draftId, languageManager.getLocalizedString(
                        "ClaireLang.Embed.Commands.Components.Regular.Voting.Cancel")));
    }

    public static ActionRow getOptionActionRow(int optionNumber, LanguageManager languageManager, boolean required) {
        languageManager.addContext(ContextManager.ContextType.GENERIC, "voting.optionnumber", optionNumber);
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.OptionLabelTemplate");
        return new ActionRowBuilder()
                .addComponents(new TextInputBuilder(TextInputStyle.SHORT, "option-" + optionNumber, label)
                        .setRequired(required).setMaximumLength(90).build())
                .build();
    }
}
