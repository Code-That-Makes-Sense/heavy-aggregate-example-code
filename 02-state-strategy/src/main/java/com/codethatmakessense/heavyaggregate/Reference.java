package com.codethatmakessense.heavyaggregate;

public record Reference(String url, String title) {

    // In real code we'd validate URL format in the canonical constructor and
    // reject bad inputs early. Kept as a method here so the publish-time guard
    // pipeline (and tests) can exercise the predicate; the post is for teaching
    // pattern shape, not URL validation.
    public boolean valid() {
        return url != null && url.startsWith("https://");
    }
}
