package com.codethatmakessense.heavyaggregate;

public record Decision(boolean allowed, String reason) {

    public static Decision ok() {
        return new Decision(true, "");
    }

    public static Decision no(String reason) {
        return new Decision(false, reason);
    }
}
