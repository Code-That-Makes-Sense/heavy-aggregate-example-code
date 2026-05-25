package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;

final class AnySpec implements Spec {

    private final Spec[] specs;

    AnySpec(Spec[] specs) {
        this.specs = specs;
    }

    public Decision check() {
        Decision lastFailure = Decision.no("no specs to satisfy");
        for (Spec s : specs) {
            Decision d = s.check();
            if (d.allowed()) {
                return d;
            }
            lastFailure = d;
        }
        return lastFailure;
    }
}
