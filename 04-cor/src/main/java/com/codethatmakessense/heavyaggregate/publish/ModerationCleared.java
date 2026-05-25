package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;

final class ModerationCleared implements PublishGuard {

    public Decision check(PublishCheck context) {
        if (context.moderationStatus() != ModerationStatus.CLEARED) {
            return Decision.no("moderation status is not cleared: " + context.moderationStatus());
        }
        return Decision.ok();
    }
}
