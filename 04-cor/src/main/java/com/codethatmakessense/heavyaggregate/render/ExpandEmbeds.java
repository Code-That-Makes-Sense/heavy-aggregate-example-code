package com.codethatmakessense.heavyaggregate.render;

final class ExpandEmbeds implements RenderLayer {

    private final RenderLayer inner;

    ExpandEmbeds(RenderLayer inner) {
        this.inner = inner;
    }

    public String apply(String input) {
        String previous = inner.apply(input);
        return "[" + previous + " + ExpandEmbeds]";
    }
}
