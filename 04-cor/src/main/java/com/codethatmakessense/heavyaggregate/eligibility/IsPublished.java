package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.Status;

final class IsPublished implements Spec {

    private final Status status;

    IsPublished(Status status) {
        this.status = status;
    }

    public Decision check() {
        if (status != Status.PUBLISHED) {
            return Decision.no("post must be published");
        }
        return Decision.ok();
    }
}
