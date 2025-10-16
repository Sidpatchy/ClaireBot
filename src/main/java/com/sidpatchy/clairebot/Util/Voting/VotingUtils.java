package com.sidpatchy.clairebot.Util.Voting;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;

public class VotingUtils {
    public static String getPollID(Boolean allowMultipleChoices, String authorID, String numChoices) {
        long timestamp = System.currentTimeMillis() / 1000L;
        long authorIdLong = Long.parseLong(authorID);
        int numChoicesInt = Integer.parseInt(numChoices);

        // Pack into bytes: 4 bytes timestamp + 8 bytes authorID + 1 byte flags/numChoices
        ByteBuffer buffer = ByteBuffer.allocate(13);
        buffer.putInt((int) timestamp);  // 4 bytes (will work until 2038)
        buffer.putLong(authorIdLong);    // 8 bytes

        // Pack allowMultipleChoices and numChoices into 1 byte
        byte packed = (byte) ((allowMultipleChoices ? 0x80 : 0) | (numChoicesInt & 0x7F));
        buffer.put(packed);

        // Base64 encode (URL-safe, no padding)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    /**
     * Extracts the poll ID from a footer string in a locale-agnostic way.
     * Strategy: take the substring after the last colon if present, then the last whitespace-delimited token.
     */
    public static String extractPollIdFromFooter(String footerText) {
        if (footerText == null) return "";
        String s = footerText;
        int colon = s.lastIndexOf(':');
        if (colon >= 0 && colon + 1 < s.length()) {
            s = s.substring(colon + 1);
        }
        s = s.trim();
        int space = s.lastIndexOf(' ');
        if (space >= 0 && space + 1 < s.length()) {
            s = s.substring(space + 1).trim();
        }
        return s;
    }

    public static HashMap<String, String> parsePollID(String pollID) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(pollID);
            if (bytes.length < 13) {
                throw new IllegalArgumentException("Unexpected poll ID length: " + bytes.length);
            }
            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            long timestamp = Integer.toUnsignedLong(buffer.getInt());
            long authorId = buffer.getLong();
            byte packed = buffer.get();

            boolean allowMultipleChoices = (packed & 0x80) != 0;
            int numChoices = packed & 0x7F;

            return new HashMap<>() {{
                put("timestamp", String.valueOf(timestamp));
                put("allowMultipleChoices", allowMultipleChoices ? "1" : "0");
                put("authorID", String.valueOf(authorId));
                put("numChoices", String.valueOf(numChoices));
            }};
        } catch (Exception e) {
            // Graceful fallback: default to allowing multiple choices to avoid over-restricting users
            return new HashMap<>() {{
                put("timestamp", "0");
                put("allowMultipleChoices", "1");
                put("authorID", "0");
                put("numChoices", "0");
            }};
        }
    }
}