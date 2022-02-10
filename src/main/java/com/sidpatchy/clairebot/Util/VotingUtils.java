package com.sidpatchy.clairebot.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VotingUtils {
    public static String getPollID(Boolean allowMultipleChoices, String authorID, String numChoices) {
        StringBuilder pollID = new StringBuilder(System.currentTimeMillis() / 1000L + ":");

        if (allowMultipleChoices) { pollID.append(1); }
        else { pollID.append(0); }

        pollID.append(":");
        pollID.append(authorID);
        pollID.append(":");
        pollID.append(numChoices);

        return pollID.toString();
    }

    public static HashMap<String, String> parsePollID(String pollID) {
        List<String> entries = Arrays.asList(StringUtils.splitPreserveAllTokens(pollID, ":"));

        return new HashMap<>() {{
            put("timestamp", entries.get(0));
            put("allowMultipleChoices", entries.get(1));
            put("authorID", entries.get(2));
            put("numChoices", entries.get(3));
        }};
    }
}