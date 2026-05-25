package com.codethatmakessense.heavyaggregate.render;

final class SyntaxHighlight implements RenderLayer {

    private final RenderLayer inner;

    SyntaxHighlight(RenderLayer inner) {
        this.inner = inner;
    }

    public String apply(String input) {
        String previous = inner.apply(input);
        return "[" + previous + " + SyntaxHighlight]";
    }
}
