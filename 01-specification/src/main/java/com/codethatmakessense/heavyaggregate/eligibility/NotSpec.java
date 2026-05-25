package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;

final class NotSpec implements Spec {

    private final Spec inner;

    NotSpec(Spec inner) {
        this.inner = inner;
    }

    public Decision check() {
        if (inner.check().allowed()) {
            return Decision.no("negated condition was satisfied");
        }
        return Decision.ok();
    }
}
