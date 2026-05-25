package com.codethatmakessense.heavyaggregate.excerpt;

final class SmartTruncate implements ExcerptStrategy {

    public String extract(String body, String title) {
        return "[SmartTruncate excerpt of: " + title + " (body length " + body.length() + ")]";
    }
}
