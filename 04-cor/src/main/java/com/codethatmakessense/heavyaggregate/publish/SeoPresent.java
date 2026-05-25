package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;

final class SeoPresent implements PublishGuard {

    public Decision check(PublishCheck context) {
        if (context.seoDescription() == null || context.seoDescription().isBlank()) {
            return Decision.no("SEO description must be present");
        }
        return Decision.ok();
    }
}
