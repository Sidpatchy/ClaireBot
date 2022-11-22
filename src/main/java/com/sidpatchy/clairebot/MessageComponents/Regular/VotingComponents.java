package com.sidpatchy.clairebot.MessageComponents.Regular;

import org.javacord.api.entity.message.component.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VotingComponents {

    // Question row
    public static ActionRow getQuestionRow() {
        return ActionRow.of(TextInput.create(TextInputStyle.SHORT, "question-modal", "Question"));
    }

    // Details row
    public static ActionRow getDetailsRow() {
        return ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "details-modal", "Details"));
    }


    // Second Menu
    // Multiple choices row
    public static ActionRow getMultipleChoicesRow(String commandName) {
        return ActionRow.of(SelectMenu.create("multiple-choice", "Click to choose option", 1, 1,
                Arrays.asList(SelectMenuOption.create("Yes", "Opens menu to select more options"),
                        SelectMenuOption.create("No", "Submits " + commandName + "after clicking submit"))));
    }



    // Third Menu, if chosen
    public static List<ActionRow> getSecondMenu() {
        List<ActionRow> actionRows = new ArrayList<>();

        // Allow selecting multiple choices?
        actionRows.add(new ActionRowBuilder()
                .addComponents(SelectMenu.create("allow-multiple-choices", "Click to choose option", 1, 1,
                        Arrays.asList(SelectMenuOption.create("Yes", "Allows the respondent to select more than one option"),
                                SelectMenuOption.create("No", "Only allows the respondent to select one option"))))
                .build());

        // Populate options rows
        for (int i = 0; i < 10; i++) {
            actionRows.add(getOptionActionRow(i));
        }

        return actionRows;
    }

    public static ActionRow getOptionActionRow(int optionNumber) {
        return new ActionRowBuilder()
                .addComponents(TextInput.create(TextInputStyle.SHORT, "option-" + optionNumber, "Option #" + optionNumber))
                .build();
    }
}
