package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;

final class BodyMinLength implements PublishGuard {

    private final int minimum;

    BodyMinLength(int minimum) {
        this.minimum = minimum;
    }

    public Decision check(PublishCheck context) {
        if (context.body() == null || context.body().length() < minimum) {
            return Decision.no("body must be at least " + minimum + " characters");
        }
        return Decision.ok();
    }
}
