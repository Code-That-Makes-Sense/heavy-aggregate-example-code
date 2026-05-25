package com.codethatmakessense.heavyaggregate.excerpt;

final class FirstParagraph implements ExcerptStrategy {

    public String extract(String body, String title) {
        return "[FirstParagraph excerpt of: " + title + " (body length " + body.length() + ")]";
    }
}
