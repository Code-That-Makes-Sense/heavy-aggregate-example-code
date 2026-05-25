package com.codethatmakessense.heavyaggregate.render;

final class MarkdownToHtml implements RenderLayer {

    private final RenderLayer inner;

    MarkdownToHtml(RenderLayer inner) {
        this.inner = inner;
    }

    public String apply(String input) {
        String previous = inner.apply(input);
        return "[" + previous + " + MarkdownToHtml]";
    }
}
