package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.eligibility.HasContent;

final class TitlePresent implements PublishGuard {

    public Decision check(PublishCheck context) {
        return new HasContent(context.title(), "title").check();
    }
}
