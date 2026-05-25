package com.codethatmakessense.heavyaggregate.render;

final class SanitizeXss implements RenderLayer {

    private final RenderLayer inner;

    SanitizeXss(RenderLayer inner) {
        this.inner = inner;
    }

    public String apply(String input) {
        String previous = inner.apply(input);
        return "[" + previous + " + SanitizeXss]";
    }
}
