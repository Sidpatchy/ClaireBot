package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Lang.ContextManager;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;

public class VotingComponents {

    // Question row
    public static Label getQuestionRow(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.Question");
        return Label.of(label, TextInput.create("question-modal", TextInputStyle.SHORT)
                .setRequired(true).setMaxLength(250).build());
    }

    // Details row
    public static Label getDetailsRow(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.Details");
        return Label.of(label, TextInput.create("details-modal", TextInputStyle.PARAGRAPH)
                .setRequired(false).setMaxLength(1000).build());
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

    public static Label getOptionActionRow(int optionNumber, LanguageManager languageManager, boolean required) {
        languageManager.addContext(ContextManager.ContextType.GENERIC, "voting.optionnumber", optionNumber);
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.Voting.OptionLabelTemplate");
        return Label.of(label, TextInput.create("option-" + optionNumber, TextInputStyle.SHORT)
                .setRequired(required).setMaxLength(90).build());
    }
}
