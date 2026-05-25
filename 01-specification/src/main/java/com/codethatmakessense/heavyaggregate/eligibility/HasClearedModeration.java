package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;

final class HasClearedModeration implements Spec {

    private final ModerationStatus status;

    HasClearedModeration(ModerationStatus status) {
        this.status = status;
    }

    public Decision check() {
        if (status != ModerationStatus.CLEARED) {
            return Decision.no("moderation status is not cleared: " + status);
        }
        return Decision.ok();
    }
}
