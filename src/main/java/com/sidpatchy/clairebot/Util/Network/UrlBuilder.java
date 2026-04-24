package com.sidpatchy.clairebot.Util.Network;

public class UrlBuilder {
    /**
     * Safely joins URL path segments, ensuring proper slash placement.
     * Removes trailing slash from base and ensures single slash between segments.
     *
     * @param base the base URL (e.g., "http://api.example.com/")
     * @param segments path segments to append
     * @return properly formatted URL
     */
    public static String buildUrl(String base, String... segments) {
        if (base == null) {
            throw new IllegalArgumentException("Base URL cannot be null");
        }

        // Remove trailing slash from base if present
        String url = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;

        // Append each segment with a leading slash
        for (String segment : segments) {
            if (segment != null && !segment.isEmpty()) {
                // Remove leading slash from segment if present to avoid double slashes
                String cleanSegment = segment.startsWith("/") ? segment.substring(1) : segment;
                url += "/" + cleanSegment;
            }
        }

        return url;
    }
}
