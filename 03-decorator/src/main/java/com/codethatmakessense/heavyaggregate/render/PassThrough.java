package com.codethatmakessense.heavyaggregate.render;

final class PassThrough implements RenderLayer {

    public String apply(String input) {
        return input;
    }
}
