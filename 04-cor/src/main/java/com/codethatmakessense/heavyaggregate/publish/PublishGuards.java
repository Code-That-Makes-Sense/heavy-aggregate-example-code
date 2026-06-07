package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;

import java.util.List;

public final class PublishGuards {

    private static final List<PublishGuard> CHAIN = List.of(
        new TitlePresent(),
        new BodyMinLength(50),
        new NoBrokenReferences(),
        new SeoPresent(),
        new ModerationCleared()
    );

    private PublishGuards() {
    }

    public static Decision checkAll(PublishCheck context) {
        for (PublishGuard guard : CHAIN) {
            Decision d = guard.check(context);
            if (!d.allowed()) {
                return d;
            }
        }
        return Decision.ok();
    }
}
