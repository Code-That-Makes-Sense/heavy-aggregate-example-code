package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;

final class AllSpec implements Spec {

    private final Spec[] specs;

    AllSpec(Spec[] specs) {
        this.specs = specs;
    }

    public Decision check() {
        for (Spec s : specs) {
            Decision d = s.check();
            if (!d.allowed()) {
                return d;
            }
        }
        return Decision.ok();
    }
}
