package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;

interface Spec {

    Decision check();

    default Spec and(Spec other) {
        return Spec.all(this, other);
    }

    default Spec or(Spec other) {
        return Spec.any(this, other);
    }

    default Spec not() {
        return new NotSpec(this);
    }

    static Spec all(Spec... specs) {
        return new AllSpec(specs);
    }

    static Spec any(Spec... specs) {
        return new AnySpec(specs);
    }
}
