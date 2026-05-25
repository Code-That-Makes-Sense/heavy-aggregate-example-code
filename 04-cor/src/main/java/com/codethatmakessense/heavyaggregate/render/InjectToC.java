package com.codethatmakessense.heavyaggregate.render;

final class InjectToC implements RenderLayer {

    private final RenderLayer inner;

    InjectToC(RenderLayer inner) {
        this.inner = inner;
    }

    public String apply(String input) {
        String previous = inner.apply(input);
        return "[" + previous + " + InjectToC]";
    }
}
