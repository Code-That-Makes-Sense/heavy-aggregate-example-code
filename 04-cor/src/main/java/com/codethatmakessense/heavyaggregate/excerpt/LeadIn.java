package com.codethatmakessense.heavyaggregate.excerpt;

final class LeadIn implements ExcerptStrategy {

    public String extract(String body, String title) {
        return "[LeadIn excerpt of: " + title + " (body length " + body.length() + ")]";
    }
}
