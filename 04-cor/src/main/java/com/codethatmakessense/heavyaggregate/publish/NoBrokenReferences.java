package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.Reference;

final class NoBrokenReferences implements PublishGuard {

    public Decision check(PublishCheck context) {
        for (Reference reference : context.references()) {
            if (!reference.valid()) {
                return Decision.no("reference is broken: " + reference.url());
            }
        }
        return Decision.ok();
    }
}
